package io.averkhoglyad.popug.tasks.endpoint

import io.averkhoglyad.popug.tasks.event.Action.*
import io.averkhoglyad.popug.tasks.event.UserDto
import io.averkhoglyad.popug.tasks.event.toEntity
import io.averkhoglyad.popug.tasks.event.toId
import io.averkhoglyad.popug.tasks.service.UserService
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import kotlin.reflect.full.declaredFunctions

@Component
class UserModificationHandler(
    private val service: UserService
) {

    fun handleUserModification(message: Message<UserDto>) {

        this::class.declaredFunctions

        val dto = message.payload
        when(dto.action) {
            CREATED -> service.create(dto.toEntity())
            UPDATED -> service.update(dto.toEntity())
            DELETED -> service.delete(dto.toId())
        }
    }
}