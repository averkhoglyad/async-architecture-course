package io.averkhoglyad.popug.tasks.endpoint

import io.averkhoglyad.popug.tasks.event.UserDto
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class UserModificationHandler {

    fun handleUserModification(message: Message<UserDto>) {
        TODO("Handle User changes")
    }

}