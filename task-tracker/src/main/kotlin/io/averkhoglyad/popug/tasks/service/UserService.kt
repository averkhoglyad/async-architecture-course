package io.averkhoglyad.popug.tasks.service

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
    fun create(user: UserEntity) {
        repository.save(user)
    }

    @Transactional
    fun update(user: UserEntity) {
        repository.save(user)
    }

    @Transactional
    fun delete(userId: UUID) {
        val user = repository.findById(userId).orElse(null) ?: return
        user.isActive = false
        repository.save(user)
    }
}