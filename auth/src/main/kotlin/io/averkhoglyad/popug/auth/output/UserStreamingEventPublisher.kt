package io.averkhoglyad.popug.auth.output

import io.averkhoglyad.popug.auth.entity.UserEntity
import io.averkhoglyad.popug.auth.util.log4j
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component
import java.util.UUID

typealias UserEvent = Pair<CudEvent, UserEntity>

@Component
class UserEventPublisher(
    private val streamBridge: StreamBridge
) : EventPublisher<UserEvent> {

    private val logger by log4j()

    private val bindingName = "streamingUser"

    override fun emit(event: UserEvent) {
        val (eventName, user) = event
        val dto = user.toDto()
        logger.debug("Sending streaming message {} for User#{}: {}", eventName, user.publicId, dto)
        streamBridge.send(bindingName, GenericMessage(dto, mapOf("X-Event-Name" to eventName.toString())))
    }
}

fun EventPublisher<UserEvent>.created(user: UserEntity) = this.emit(CudEvent.CREATED to user)

fun EventPublisher<UserEvent>.updated(user: UserEntity) = this.emit(CudEvent.UPDATED to user)

fun EventPublisher<UserEvent>.deleted(user: UserEntity) = this.emit(CudEvent.DELETED to user)

data class UserDto(
    val publicId: UUID,
    val login: String,
    val name: String,
    val role: String
)

private fun UserEntity.toDto(): UserDto = UserDto(
    publicId = this.publicId,
    login = this.login,
    name = this.name,
    role = this.role.name,
)
