package main.model.request

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
