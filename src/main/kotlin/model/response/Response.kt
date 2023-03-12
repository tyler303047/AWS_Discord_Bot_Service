package main.model.response

data class Response(
    val statusCode: Int? = 200,
    val headers: Map<String, String>? = hashMapOf(),
    val body: String? = "",
)
