package main.com.tyler.awsDiscordBot

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
            return ErrorObject(401, "invalid request signature")
        }

        val bodyObject = objectMapper.readValue(event.body, DiscordBodyObject::class.java)

        if (bodyObject.type == "1") {
            println("Sent OK Response")
            return Response(200, mapOf("Content-Type" to "application/json"), JSONObject(mapOf("type" to 1)).toString())
        } else if (bodyObject.type == "2" && bodyObject.data?.name == "ping") {
            println("Sent Ping Response")
            return Response(200, mapOf("Content-Type" to "application/json"), JSONObject(mapOf(
                "type" to 4,
                "data" to mapOf(
                    "content" to "PONG!",
                )
            )).toString())
        } else {
            println("unsupported interaction found")
            return ErrorObject(400, "unsupported interaction type")
        }
    }
}

fun ErrorObject(statusCode: Int, ErrorMessage: String): Response {
    return Response(statusCode, null, JSONObject(mapOf("error" to ErrorMessage)).toString())
}

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}
