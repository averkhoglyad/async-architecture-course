package io.averkhoglyad.popug.tasks.event

import io.averkhoglyad.popug.tasks.persistence.entity.UserEntity
import io.averkhoglyad.popug.tasks.persistence.entity.UserRole
import java.util.UUID

data class UserDto(
    val action: Action,
    val id: String,
    val name: String,
    val role: String
)


enum class Action {
    CREATE,
    UPDATE,
    DELETE
}

fun UserDto.toEntity(): UserEntity = UserEntity().also {
    it.id = UUID.fromString(this.id)
    it.name = this.name
    it.role = UserRole.valueOf(this.role)
}

fun UserDto.toId(): UUID = UUID.fromString(this.id)