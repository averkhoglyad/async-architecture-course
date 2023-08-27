package io.averkhoglyad.popug.schema

import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import kotlin.jvm.Throws

class JsonSchemaValidator(
    private val objectMapper: ObjectMapper
): SchemaValidator {

    @Throws(InvalidSchemaException::class)
    override fun validate(schema: String, version: String, message: ByteArray) {
        val factory: JsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4)

        // TODO: Cache schemas
        val jsonSchema = javaClass.getResourceAsStream("/schemas/${schema.lowercase()}/${version}.json") // Lowercase because of the win filesystem
            .use { factory.getSchema(it) }
        val tree = objectMapper.readTree(message)

        val validationResult = jsonSchema.validate(tree)
        if (validationResult.isEmpty()) {
            return
        }
        // TODO: Log errors
        // TODO: Return errors in exception
        throw InvalidSchemaException(schema, version)
    }

}