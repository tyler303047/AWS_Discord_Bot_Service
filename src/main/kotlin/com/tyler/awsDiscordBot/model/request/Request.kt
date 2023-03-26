package main.model.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class Request(
    var version: Double = 1.0,
    var resource: String = "",
    var path: String = "",
    var httpMethod: HttpMethod = HttpMethod.POST,
    var headers: Map<String, String> = mapOf<String, String>(),
    var multiValueHeaders: Map<String, Array<String>> = mapOf<String, Array<String>>(),
    var queryStringParameters: String? = "",
    var multiValueQueryStringParameters: String? = "",
    var requestContext: RequestContext = RequestContext(),
    var pathParameters: String? = "",
    var stageVariables: String? = "",
    var body: String? = "",
    var isBase64Encoded: Boolean? = false,
)

data class RequestContext(
    var accountId: String = "",
    var apiId: String = "",
    var domainName: String = "",
    var domainPrefix: String = "",
    var extendedRequestId: String = "",
    var httpMethod: HttpMethod = HttpMethod.POST,
    var identity: RequestIdentity = RequestIdentity(),
    var path: String = "",
    var protocol: String = "",
    var requestId: String = "",
    var requestTime: String = "",
    var requestTimeEpoch: String = "",
    var resourceId: String = "",
    var resourcePath: String = "",
    var stage: String = "",
)

data class RequestIdentity(
    var accessKey: String? = "",
    var accountId: String? = "",
    var caller: String? = "",
    var cognitoAmr: String? = "",
    var cognitoAuthenticationProvider: String? = "",
    var cognitoAuthenticationType: String? = "",
    var cognitoIdentityId: String? = "",
    var cognitoIdentityPoolId: String? = "",
    var principalOrgId: String? = "",
    var sourceIp: String? = "",
    var user: String? = "",
    var userAgent: String? = "",
    var userArn: String? = "",
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
    val guild_id: String?,
    val channel_id: String?,
    val member: GuildMemberObject?,
    val user: UserObject?,
    val token: String,
    val version: Int,
    val message: MessageObject?,
    val locale: String?,
    val guild_locale: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscordDataObject(
    val id: String,
    val name: String,
    val type: Int,
    val resolved: String?,
    val options: Array<String>?,
    val guild_id: String?,
    val target_id: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GuildMemberObject(
    val user: UserObject?,
    val joined_at: String?,
    val deaf: Boolean?,
    val mute: Boolean?,
    val flags: Int?,
    val pending: Boolean?,
    val permissions: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserObject(
    val id: String,
    val username: String,
    val discriminator: String?,
    val avatar: String?,
    val bot: Boolean?,
    val system: Boolean?,
    val banner: String?,
    val locale: String?,
    val email: String?,
    val flags: Int?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageObject(
    val id: String,
    val channel_id: String,
    val author: UserObject,
    val content: String,
    val timestamp: String,
    val edited_timestamp: String,
    val webhook_id: String?,
    val type: Int,
    val application: String?,
    val application_id: String?,
)
