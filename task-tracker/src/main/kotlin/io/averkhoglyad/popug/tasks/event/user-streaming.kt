package io.averkhoglyad.popug.tasks.event

import io.averkhoglyad.popug.tasks.persistence.entity.UserEntity
import io.averkhoglyad.popug.tasks.persistence.entity.UserRole
import java.util.UUID

data class UserDto(
    val publicId: UUID,
    val name: String,
    val role: String
)

fun UserDto.toEntity(entity: UserEntity): UserEntity = entity
    .also {
        it.publicId = this.publicId
        it.name = this.name
        it.role = UserRole.valueOf(this.role)
    }
