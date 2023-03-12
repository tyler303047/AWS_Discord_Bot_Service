package main

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import main.model.request.Request
import main.model.response.Response
import software.pando.crypto.nacl.Crypto

class MainHandler: RequestHandler<Request, Response> {

    private val publicKeyString = System.getenv("PUBLIC_KEY")

    override fun handleRequest(event: Request, context: Context): Response {
        println("Handling request: $event")

        val isVerified = Crypto.signVerify(
            Crypto.signingPublicKey(publicKeyString.decodeHex()),
            (event.headers["x-signature-timestamp"] + event.body).toByteArray(),
            event.headers["x-signature-ed25519"]?.decodeHex()
        )

        if (!isVerified) {
            println("Sent Invalid Request Response")
            return Response(401, null, "invalid request signature")
        }

        println("Sent OK Response")
        return Response(200, null, "OK")
    }
}

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}