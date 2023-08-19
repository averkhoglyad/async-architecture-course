package io.averkhoglyad.popug.tasks.service

import io.averkhoglyad.popug.tasks.event.UserDto
import io.averkhoglyad.popug.tasks.event.toEntity
import io.averkhoglyad.popug.tasks.persistence.entity.UserEntity
import io.averkhoglyad.popug.tasks.persistence.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val repository: UserRepository
) {

    @Transactional
    fun create(dto: UserDto) {
        repository
            .save(dto.toEntity(UserEntity()))
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