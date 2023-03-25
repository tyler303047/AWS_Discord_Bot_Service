package main.model.request

import java.sql.Timestamp

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
    val app_permissions: String?,
    val locale: String?,
    val guild_locale: String?,
)

data class DiscordDataObject(
    val id: String,
    val name: String,
    val type: Int,
    val resolved: String?,
    val options: Array<String>?,
    val guild_id: String?,
    val target_id: String?,
)

data class GuildMemberObject(
    val user: UserObject?,
    val nick: String?,
    val avatar: String?,
    val roles: Array<String>?,
    val joined_at: String?,
    val premium_since: String?,
    val deaf: Boolean?,
    val mute: Boolean?,
    val flags: Int?,
    val pending: Boolean?,
    val permissions: String?,
    val communication_disabled_until: String?,
)

data class UserObject(
    val id: String,
    val username: String,
    val display_name: String?,
    val discriminator: String?,
    val avatar: String?,
    val avatar_decoration: String?,
    val bot: Boolean?,
    val system: Boolean?,
    val mfa_enabled: Boolean?,
    val banner: String?,
    val accent_color: Int?,
    val locale: String?,
    val verified: Boolean?,
    val email: String?,
    val flags: Int?,
    val premium_type: Int?,
    val public_flags: Int?,
)

data class MessageObject(
    val id: String,
    val channel_id: String,
    val author: UserObject,
    val content: String,
    val timestamp: String,
    val edited_timestamp: String,
    val tts: Boolean,
    val mention_everyone: Boolean,
    val mentions: Array<UserObject>,
    val mention_roles: Array<String>,
    val mention_channels: Array<String>?,
    val attachments: Array<String>,
    val embeds: Array<String>,
    val reations: Array<String>?,
    val nonce: String,
    val pinned: String,
    val webhook_id: String?,
    val type: Int,
    val activty: String?,
    val application: String?,
    val application_id: String?,
    val message_reference: String?,
    val interaction: String?,
    val thread: String?,
    val components: Array<String>?,
    val sticker_items: Array<String>?,
    val position: Int?,
    val role_subscription_data: String?,
)
