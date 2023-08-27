package io.averkhoglyad.popug.schema

import kotlin.jvm.Throws

interface SchemaValidator {

    @Throws(InvalidSchemaException::class)
    fun validate(schema: String, version: String, message: ByteArray)

}