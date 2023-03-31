package com.tyler.awsDiscordBot.orchestration

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import main.model.request.DiscordBodyObject
import main.model.request.Request
import main.model.response.*
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.pando.crypto.nacl.Crypto

class OrchestrationLambdaHandler(
    publicKeyStringConstructor: String? = System.getenv("PUBLIC_KEY"),
    snsArnConstructor: String? = System.getenv("SNS_ARN"),
    snsClientConstructor: SnsClient? = SnsClient.builder()
        .region(Region.of(System.getenv("AWS_REGION")))
        .build()
): RequestHandler<Request, SerializedResponse> {

    private val objectMapper = jacksonObjectMapper()
    private val publicKeyString = publicKeyStringConstructor ?: System.getenv("PUBLIC_KEY")
    private val snsArn = snsArnConstructor ?: System.getenv("SNS_ARN")
    private val snsClient = snsClientConstructor ?: SnsClient.builder()
        .region(Region.of(System.getenv("AWS_REGION")))
        .build()

    override fun handleRequest(event: Request, context: Context?): SerializedResponse {
        println("Handling request: $event")

        println("publicKeyString: $publicKeyString")

        println("${event.body}")

        val isVerified = Crypto.signVerify(
            Crypto.signingPublicKey(publicKeyString.decodeHex()),
            (event.headers["x-signature-timestamp"] + event.body).toByteArray(),
            event.headers["x-signature-ed25519"]?.decodeHex()
        )

        println("isVerified: $isVerified")

        if (!isVerified) {
            println("Sent Invalid Request Response")
            return errorObject(401, "invalid request signature")
        }

        println("Before deserialization")
        val bodyObject = objectMapper.readValue(event.body, DiscordBodyObject::class.java)
        println("After deserialization7")

        return fanOutByType(bodyObject).also {
            println("Serialized Response: $it")
        }
    }

    private fun fanOutByType(bodyObject: DiscordBodyObject): SerializedResponse {
        return when(bodyObject.type) {
            "1" -> verifyResponse()
            "2" -> fanOutBySlashCommand(bodyObject)
            else -> errorObject(400, "unsupported interaction type").also {
                println("unsupported interaction found")
            }
        }
    }

    private fun verifyResponse(): SerializedResponse {
        println("Sent OK Response")
        return VerifyResponse().toSerialized(objectMapper)
    }

    private fun pingResponse(): SerializedResponse {
        println("Sent Ping Response")

        return PongResponse().toSerialized(objectMapper)
    }

    private fun fanOutBySlashCommand(bodyObject: DiscordBodyObject): SerializedResponse {
        return when(bodyObject.data!!.name) {
            "ping" -> pingResponse()
            "ls-add" -> addSimCommand(bodyObject)
            else -> errorObject(400, "unsupported slash command type").also {
                println("unsupported slash command found")
            }
        }
    }

    private fun addSimCommand(bodyObject: DiscordBodyObject): SerializedResponse {
        val publishRequest = PublishRequest.builder()
            .messageAttributes(
                mapOf(
                    "command_type" to MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue("add-command")
                        .build()
                )
            )
            .message(objectMapper.writeValueAsString(bodyObject))
            .topicArn(snsArn)
            .build()

        snsClient.publish(publishRequest).also {
            println("Publishing to SNS Topic: $it")
        }

        return DeferringResponse().toSerialized(objectMapper)
    }

    private fun errorObject(statusCode: Int, errorMessage: String): SerializedResponse {
        return ErrorResponse(statusCode, null, ErrorBody(errorMessage)).toSerialized(objectMapper)
    }

    private fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }
}


