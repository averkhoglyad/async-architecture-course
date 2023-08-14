package io.averkhoglyad.popug.auth.output

import io.averkhoglyad.popug.auth.entity.UserEntity
import io.averkhoglyad.popug.auth.util.log4j
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Component

typealias UserEvent = Pair<Action, UserEntity>

@Component
class UserEventPublisher(
    private val streamBridge: StreamBridge
) : EventPublisher<UserEvent> {

    private val logger by log4j()

    private val bindingName = "userModified"

    override fun emit(event: UserEvent) {
        val (action, user) = event
        val dto = user.toDto(action)
        logger.debug("Sending message {} for User: {}", action, dto)
        streamBridge.send(bindingName, dto)
    }
}

fun EventPublisher<UserEvent>.emit(action: Action, user: UserEntity) = this.emit(action to user)

data class UserDto(
    val action: Action,
    val id: String,
    val login: String,
    val name: String,
    val role: String
)

private fun UserEntity.toDto(action: Action): UserDto = UserDto(
    action = action,
    id = this.id.toString(),
    login = this.login,
    name = this.name,
    role = this.role.name,
)
