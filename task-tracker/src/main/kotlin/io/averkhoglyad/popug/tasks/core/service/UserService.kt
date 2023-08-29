package io.averkhoglyad.popug.tasks.core.service

import io.averkhoglyad.popug.tasks.core.event.UserDto
import io.averkhoglyad.popug.tasks.core.persistence.entity.UserEntity
import io.averkhoglyad.popug.tasks.core.persistence.entity.UserRole
import io.averkhoglyad.popug.tasks.core.persistence.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val repository: UserRepository
) {

    @Transactional
    fun create(dto: UserDto) {
        repository
            .save(dto.toEntity())
    }

    @Transactional
    fun update(dto: UserDto) {
        repository
            .findByPublicId(dto.publicId)
            .ifPresent { repository.save(dto.toEntity(it)) }
    }

    @Transactional
    fun delete(userPublicId: UUID) {
        repository
            .findByPublicId(userPublicId)
            .ifPresent { repository.save(it.apply { isActive = false }) }
    }
}

fun UserDto.toEntity(): UserEntity = toEntity(UserEntity())

fun UserDto.toEntity(entity: UserEntity): UserEntity = entity
    .also {
        it.publicId = this.publicId
        it.name = this.name
        it.role = UserRole.parse(this.role)
    }
