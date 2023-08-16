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

    private val bindingName = "streamingUser"

    override fun emit(event: UserEvent) {
        val (action, user) = event
        val dto = event.toDto()
        logger.debug("Sending streaming message {} for User#{}: {}", action, user, dto)
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

private fun UserEvent.toDto(): UserDto = UserDto(
    action = first,
    id = second.id.toString(),
    login = second.login,
    name = second.name,
    role = second.role.name,
)
