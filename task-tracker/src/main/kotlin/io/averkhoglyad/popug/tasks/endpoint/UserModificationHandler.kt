package io.averkhoglyad.popug.tasks.endpoint

import io.averkhoglyad.popug.tasks.event.Action.*
import io.averkhoglyad.popug.tasks.event.UserDto
import io.averkhoglyad.popug.tasks.event.toEntity
import io.averkhoglyad.popug.tasks.event.toId
import io.averkhoglyad.popug.tasks.service.UserService
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class UserModificationHandler(
    private val service: UserService
) {

    fun handleUserModification(message: Message<UserDto>) {
        val dto = message.payload
        when(dto.action) {
            CREATE -> service.create(dto.toEntity())
            UPDATE -> service.update(dto.toEntity())
            DELETE -> service.delete(dto.toId())
        }
    }
}