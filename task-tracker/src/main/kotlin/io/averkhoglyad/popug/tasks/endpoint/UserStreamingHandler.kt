package io.averkhoglyad.popug.tasks.endpoint

import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.tasks.config.KAFKA_LISTENER_STREAMING_USER
import io.averkhoglyad.popug.tasks.core.event.CudEvent
import io.averkhoglyad.popug.tasks.core.event.CudEvent.*
import io.averkhoglyad.popug.tasks.core.event.UserDto
import io.averkhoglyad.popug.tasks.core.service.UserService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class UserStreamingHandler(
    private val service: UserService
) {

    @KafkaListener(
        topics = ["Streaming-User"],
        idIsGroup = false,
        containerFactory = KAFKA_LISTENER_STREAMING_USER,
    )
    fun handleUserModification(message: Message<UserDto>) {
        val dto = message.payload
        // TODO: Move to bindings or dispatcher analog.
        //       Handle errors on empty or wrong event name
        val eventName = message.headers[PopugKafkaHeaders.EVENT_NAME]
            .let { CudEvent.valueOf(it.toString()) }
        when (eventName) {
            CREATED -> service.create(dto)
            UPDATED -> service.update(dto)
            DELETED -> service.delete(dto.publicId)
        }
    }
}