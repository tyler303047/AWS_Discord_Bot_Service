package com.tyler.awsDiscordBot.addCommand

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import main.model.request.DiscordBodyObject
import main.model.response.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

class AddCommandLambdaHandler: RequestHandler<SNSEvent, SerializedResponse> {

    private val objectMapper = jacksonObjectMapper()

    override fun handleRequest(event: SNSEvent, context: Context?): SerializedResponse {
        val data = objectMapper.readValue(event.records.first().sns.message, DiscordBodyObject::class.java)

        HttpClient.newBuilder().build().send(
            HttpRequest.newBuilder()
                .uri(URI.create(
                    "https://discord.com/api/v10/webhooks/${data.application_id}/${data.token}/messages/@original"
                ))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(
                    DiscordPongBody(6, DiscordPongData("Thank you!"))
                )))
                .header("Content-Type", "application/json")
                .build(),
            BodyHandlers.ofString()
        ).also {
            println("Response back to Discord: ${it.body()}")
        }

        return VerifyResponse().toSerialized(objectMapper)
    }
}