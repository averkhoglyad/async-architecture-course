package io.averkhoglyad.popug.tasks.endpoint

import io.averkhoglyad.popug.tasks.event.StreamingAction.*
import io.averkhoglyad.popug.tasks.event.UserDto
import io.averkhoglyad.popug.tasks.event.toEntity
import io.averkhoglyad.popug.tasks.event.toId
import io.averkhoglyad.popug.tasks.service.UserService
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class UserStreamingHandler(
    private val service: UserService
) {

    fun handleUserModification(message: Message<UserDto>) {
        val dto = message.payload
        when(dto.action) {
            CREATED -> service.create(dto.toEntity())
            UPDATED -> service.update(dto.toEntity())
            DELETED -> service.delete(dto.toId())
        }
    }
}