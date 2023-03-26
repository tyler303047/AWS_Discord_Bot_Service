package com.tyler.awsDiscordBot.orchestration

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import main.model.request.DiscordBodyObject
import main.model.request.Request
import main.model.response.Response
import org.json.JSONObject
import software.pando.crypto.nacl.Crypto

class OrchestrationLambdaHandler: RequestHandler<Request, Response> {

    private val publicKeyString = System.getenv("PUBLIC_KEY")
    private val snsArn = System.getenv("SNS_ARN")
    private val objectMapper = jacksonObjectMapper()

    override fun handleRequest(event: Request, context: Context?): Response {
        println("Handling request: $event")

        println("publicKeyString: $publicKeyString")

        println("${event.body}")

        val isVerified = Crypto.signVerify(
            Crypto.signingPublicKey(publicKeyString.decodeHex()),
            (event.headers["x-signature-timestamp"] + event.body).toByteArray(),
            event.headers["x-signature-ed25519"]?.decodeHex()
        )

        if (!isVerified) {
            println("Sent Invalid Request Response")
            return errorObject(401, "invalid request signature")
        }

        val bodyObject = objectMapper.readValue(event.body, DiscordBodyObject::class.java)

        return fanOutByType(bodyObject)
    }

    private fun fanOutByType(bodyObject: DiscordBodyObject): Response {
        return when(bodyObject.type) {
            "1" -> verifyResponse()
            "2" -> pingResponse()
            else -> errorObject(400, "unsupported interaction type").also {
                println("unsupported interaction found")
            }
        }
    }

    private fun verifyResponse(): Response {
        println("Sent OK Response")
        return Response(200,
            mapOf("Content-Type" to "application/json"),
            JSONObject(mapOf("type" to 1)).toString())
    }

    private fun pingResponse(): Response {
        println("Sent Ping Response")

        return Response(200,
            mapOf("Content-Type" to "application/json"),
            JSONObject(mapOf(
                "type" to 4,
                "data" to mapOf(
                    "content" to "PONG!",
                )
            )).toString())
    }

    private fun errorObject(statusCode: Int, errorMessage: String): Response {
        return Response(statusCode, null, JSONObject(mapOf("error" to errorMessage)).toString())
    }

    private fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }
}


