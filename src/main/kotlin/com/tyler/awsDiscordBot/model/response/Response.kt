package main.model.response

import com.fasterxml.jackson.databind.ObjectMapper

/*
    Primitive Response
 */
data class Response(
    val statusCode: Int?,
    val headers: Header?,
    val body: String?,
)
fun Response.toSerialized(objectMapper: ObjectMapper): SerializedResponse {
    return SerializedResponse(
        statusCode = statusCode ?: 200,
        headers = headers?.toSerialized() ?: mapOf("Content-Type" to "application/json"),
        body = objectMapper.writeValueAsString(body) ?: "",
    )
}
data class Header(
    val content_type: String = "application/json",
)
fun Header.toSerialized(): Map<String, String> {
    return mapOf(
        "Content-Type" to content_type
    )
}

data class SerializedResponse(
    val statusCode: Int,
    val headers: Map<String, String>,
    val body: String,
)

/*
    Verify Response
 */
data class VerifyResponse(
    val statusCode: Int = 200,
    val headers: Header = Header(),
    val body: VerifyBody = VerifyBody(),
)
fun VerifyResponse.toSerialized(objectMapper: ObjectMapper): SerializedResponse {
    return SerializedResponse(
        statusCode,
        headers.toSerialized(),
        objectMapper.writeValueAsString(body),
    )
}
data class VerifyBody(
    val type: Int = 1,
)

/*
    Pong Response
 */
data class PongResponse(
    val statusCode: Int = 200,
    val headers: Header? = Header(),
    val body: DiscordPongBody = DiscordPongBody(),
)
fun PongResponse.toSerialized(objectMapper: ObjectMapper): SerializedResponse {
    return SerializedResponse(
        statusCode = statusCode,
        headers = headers?.toSerialized() ?: mapOf("Content-Type" to "application/json"),
        body = objectMapper.writeValueAsString(body) ?: "",
    )
}
data class DiscordPongBody(
    val type: Int = 4,
    val data: DiscordPongData = DiscordPongData(),
)
data class DiscordPongData(
    val content: String = "PONG",
)

/*
    Error Response
 */
data class ErrorResponse(
    val statusCode: Int? = 400,
    val headers: Header? = Header(),
    val body: ErrorBody? = ErrorBody(),
)
fun ErrorResponse.toSerialized(objectMapper: ObjectMapper): SerializedResponse {
    return SerializedResponse(
        statusCode = statusCode ?: 400,
        headers = headers?.toSerialized() ?: mapOf("Content-Type" to "application/json"),
        body = objectMapper.writeValueAsString(body) ?: "",
    )
}
data class ErrorBody(
    val error: String = "Internal Server Error"
)

/*
    Deferring Response
 */

data class DeferringResponse(
    val statusCode: Int = 200,
    val headers: Header? = Header(),
    val body: DeferringBody = DeferringBody(),
)
fun DeferringResponse.toSerialized(objectMapper: ObjectMapper): SerializedResponse {
    return SerializedResponse(
        statusCode,
        headers?.toSerialized() ?: mapOf("Content-Type" to "application/json"),
        objectMapper.writeValueAsString(body),
    )
}
data class DeferringBody(
    val type: Int = 5,
    val data: DiscordDeferredData = DiscordDeferredData(),
)
data class DiscordDeferredData(
    val content: String = "Loading...Please wait",
)