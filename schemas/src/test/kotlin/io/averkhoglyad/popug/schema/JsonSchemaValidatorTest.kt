package io.averkhoglyad.popug.schema

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.*

class JsonSchemaValidatorTest {

    @Test
    fun canInstantiate() {
        service()
    }

    @Test
    fun noErrorsOnCorrectSchema() {
        var json = "{ \"publicId\": \"${UUID.randomUUID()}\", \"title\": \"Asdf QWerty\", \"description\": \"Lorem ipsum\" }"
        service().validate("test.created", "v1", json.toByteArray())
        json = "{ \"publicId\": \"${UUID.randomUUID()}\", \"title\": \"Asdf QWerty\" }"
        service().validate("test.created", "v1", json.toByteArray())
    }

    @Test
    fun errorsOnUnexpectedInSchemaSchemaProperty() {
        val json = "{ \"publicId\": \"${UUID.randomUUID()}\", \"title\": \"Asdf QWerty\", \"newField\": \"Lorem ipsum\" }"
        assertThatThrownBy { service().validate("test.created", "v1", json.toByteArray()) }
            .isInstanceOf(InvalidSchemaException::class.java)
    }

    private fun service() = JsonSchemaValidator(ObjectMapper())
}
