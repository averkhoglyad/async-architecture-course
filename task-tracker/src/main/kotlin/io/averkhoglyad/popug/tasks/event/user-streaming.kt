package io.averkhoglyad.popug.tasks.event

import io.averkhoglyad.popug.tasks.persistence.entity.UserEntity
import io.averkhoglyad.popug.tasks.persistence.entity.UserRole
import java.util.UUID

data class UserDto(
    val action: StreamingAction,
    val id: String,
    val name: String,
    val role: String
)

enum class StreamingAction {
    CREATED,
    UPDATED,
    DELETED
}

fun UserDto.toEntity(): UserEntity = UserEntity().also {
    it.id = UUID.fromString(this.id)
    it.name = this.name
    it.role = UserRole.valueOf(this.role)
    it.isActive = action != StreamingAction.DELETED
}

fun UserDto.toId(): UUID = UUID.fromString(this.id)
