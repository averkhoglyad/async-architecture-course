package io.averkhoglyad.popug.common.kafka

import org.apache.kafka.common.header.Headers

object PopugKafkaHeaders {
    const val EVENT_NAME = "X-Event-Name"
    const val EVENT_VERSION = "X-Event-Version"
    const val PUBLISHED_AT = "X-Published-At"
}

fun Headers.getLastAsString(name: String): String {
    return lastHeader(name)?.value()?.toString(Charsets.UTF_8) ?: ""
}

fun Headers.addAsString(name: String, value: String) {
    add(name, value.toByteArray())
}

fun Headers.addAsString(name: String, value: Any?) {
    addAsString(name, value.toString())
}
