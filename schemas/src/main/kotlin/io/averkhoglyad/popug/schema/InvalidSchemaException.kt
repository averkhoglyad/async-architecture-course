package io.averkhoglyad.popug.schema

class InvalidSchemaException(
    val schema: String,
    val version: String,
    cause: Exception? = null): Exception(cause) {
}