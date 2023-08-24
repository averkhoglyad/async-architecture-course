package io.averkhoglyad.popug.schema.kafka

import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.common.kafka.headerAsString
import io.averkhoglyad.popug.schema.SchemaValidator
import org.apache.kafka.common.header.Headers
import org.apache.kafka.common.serialization.Serializer

class SchemaValidationSerializer<T>(
    private val root: Serializer<T>,
    private val validator: SchemaValidator
) : Serializer<T> {

    override fun close() {
        root.close()
    }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        root.configure(configs, isKey)
    }

    override fun serialize(topic: String?, data: T): ByteArray {
        return root.serialize(topic, data)
    }

    override fun serialize(topic: String, headers: Headers, data: T): ByteArray {
        val bytes = root.serialize(topic, headers, data)
        val eventNameHeader = headers.headerAsString(PopugKafkaHeaders.EVENT_NAME)
        val eventVersionHeader = headers.headerAsString(PopugKafkaHeaders.EVENT_VERSION)
        validator.validate("$topic.$eventNameHeader", eventVersionHeader, bytes)
        return bytes
    }
}
