package io.averkhoglyad.popug.tasks.endpoint

import io.averkhoglyad.popug.tasks.event.CudEvent
import io.averkhoglyad.popug.tasks.event.CudEvent.*
import io.averkhoglyad.popug.tasks.event.UserDto
import io.averkhoglyad.popug.tasks.service.UserService
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class UserStreamingHandler(
    private val service: UserService
) {

    fun handleUserModification(message: Message<UserDto>) {
        val dto = message.payload
        // TODO: Move to bindings or dispatcher analog.
        //       Handle errors on empty or wrong event name
        val eventName = message.headers["X-Event-Name"]
            .let { CudEvent.valueOf(it.toString()) }
        when (eventName) {
            CREATED -> service.create(dto)
            UPDATED -> service.update(dto)
            DELETED -> service.delete(dto.publicId)
        }
    }
}