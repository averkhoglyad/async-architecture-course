package io.averkhoglyad.popug.auth.data

data class ExceptionResponse(val error: String, val params: List<Any> = emptyList()) {
    constructor(error: String, vararg params: Any) : this(error, listOf(*params))
}