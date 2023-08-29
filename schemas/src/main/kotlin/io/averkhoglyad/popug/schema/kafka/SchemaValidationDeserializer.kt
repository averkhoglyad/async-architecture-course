package io.averkhoglyad.popug.schema.kafka

import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.common.kafka.getLastAsString
import io.averkhoglyad.popug.schema.SchemaValidator
import org.apache.kafka.common.header.Headers
import org.apache.kafka.common.serialization.Deserializer

class SchemaValidationDeserializer<T>(
    private val root: Deserializer<T>,
    private val validator: SchemaValidator
) : Deserializer<T> {

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        root.configure(configs, isKey)
    }

    override fun deserialize(topic: String?, data: ByteArray?): T {
        return root.deserialize(topic, data)
    }

    override fun deserialize(topic: String, headers: Headers, data: ByteArray): T {
        val eventNameHeader = headers.getLastAsString(PopugKafkaHeaders.EVENT_NAME)
        val eventVersionHeader = headers.getLastAsString(PopugKafkaHeaders.EVENT_VERSION)
        validator.validate("$topic.$eventNameHeader", eventVersionHeader, data)
        return root.deserialize(topic, headers, data)
    }

    override fun close() {
        root.close()
    }
}
