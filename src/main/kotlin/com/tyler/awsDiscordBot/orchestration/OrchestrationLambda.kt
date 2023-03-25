package main.com.tyler.awsDiscordBot

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import main.model.request.Request
import main.model.response.Response
import org.json.JSONObject
import software.pando.crypto.nacl.Crypto
import java.io.File

class OrchestrationLambdaHandler: RequestHandler<Request, Response> {

    private val publicKeyString = System.getenv("PUBLIC_KEY")

    override fun handleRequest(event: Request, context: Context?): Response {
        println("Handling request: $event")

        println("publicKeyString: ${publicKeyString}")

        val isVerified = Crypto.signVerify(
            Crypto.signingPublicKey(publicKeyString.decodeHex()),
            (event.headers["x-signature-timestamp"] + event.body).toByteArray(),
            event.headers["x-signature-ed25519"]?.decodeHex()
        )

        if (!isVerified) {
            println("Sent Invalid Request Response")
            return ErrorObject(401, "invalid request signature")
        }

        if (event.body["type"] == "1") {
            println("Sent OK Response")
            return Response(200, mapOf("Content-Type" to "application/json"), JSONObject(mapOf("type" to 1)).toString())
        } else if (event.body["type"] == "2") {
            return Response(200, mapOf("Content-Type" to "application/json"), JSONObject(mapOf(
                "type" to 4,
                "data" to JSONObject(mapOf(
                    "content" to "PONG!",
                )).toString()
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
