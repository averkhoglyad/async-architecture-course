package io.averkhoglyad.popug.accounting.endpoint

import io.averkhoglyad.popug.accounting.config.KAFKA_LISTENER_STREAMING_USER
import io.averkhoglyad.popug.accounting.core.event.streaming.CudEvent
import io.averkhoglyad.popug.accounting.core.event.streaming.CudEvent.*
import io.averkhoglyad.popug.accounting.core.event.streaming.UserDto
import io.averkhoglyad.popug.accounting.core.service.UserService
import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.common.log4j
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class UserStreamingHandler(
    private val service: UserService
) {

    private val log by log4j()

    @KafkaListener(
        topics = ["Streaming-User"],
        idIsGroup = false,
        containerFactory = KAFKA_LISTENER_STREAMING_USER,
    )
    fun handleUserModification(
        @Payload dto: UserDto,
        @Header(name = PopugKafkaHeaders.EVENT_NAME, required = false, defaultValue = "") eventNameHeader: String
    ) {
        val eventName = CudEvent.parse(eventNameHeader)
        when (eventName) {
            CREATED -> service.create(dto)
            UPDATED -> service.update(dto)
            DELETED -> service.delete(dto.publicId)
            // TODO: Handle errors on empty or wrong event name
            else -> log.warn("Unexpected event: ", eventNameHeader)
        }
    }
}