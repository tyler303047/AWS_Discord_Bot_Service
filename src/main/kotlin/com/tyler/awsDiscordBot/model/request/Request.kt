package main.model.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Request(
    var version: Double = 1.0,
    var resource: String = "",
    var path: String? = "",
    var httpMethod: HttpMethod = HttpMethod.POST,
    var headers: Map<String, String> = mapOf(),
    var requestContext: RequestContext = RequestContext(),
    var body: String? = "",
    var isBase64Encoded: Boolean? = false,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestContext(
    var accountId: String = "",
    var apiId: String = "",
    var domainName: String = "",
    var domainPrefix: String = "",
    var httpMethod: HttpMethod = HttpMethod.POST,
    var path: String? = "",
    var protocol: String? = "",
    var requestId: String = "",
    var requestTime: String? = "",
    var requestTimeEpoch: String? = "",
    var resourceId: String? = "",
    var resourcePath: String? = "",
    var stage: String = "",
)
enum class HttpMethod {
    POST,
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscordBodyObject(
    val id: String,
    val application_id: String,
    val type: String,
    val data: DiscordDataObject?,
    val user: UserObject?,
    val token: String,
    val version: Int,
    val message: MessageObject?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscordDataObject(
    val id: String,
    val name: String,
    val type: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserObject(
    val id: String,
    val username: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageObject(
    val id: String,
    val channel_id: String,
    val author: UserObject,
    val content: String,
    val timestamp: String,
    val edited_timestamp: String,
    val type: Int,
)
