package io.averkhoglyad.popug.auth.service

import io.averkhoglyad.popug.auth.entity.UserEntity
import io.averkhoglyad.popug.auth.output.Action
import io.averkhoglyad.popug.auth.output.UserEventPublisher
import io.averkhoglyad.popug.auth.output.emit
import io.averkhoglyad.popug.auth.repository.UserRepository
import io.averkhoglyad.popug.auth.util.transaction
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val eventPublisher: UserEventPublisher
) {

    @Transactional(readOnly = true)
    fun findList(pageable: Pageable): Page<UserEntity> {
        return repository.findAll(pageable)
    }

    @Transactional(readOnly = true)
    @Throws(EntityNotFoundException::class)
    fun findEntity(id: UUID): UserEntity {
        return repository.findById(id)
            .orElseThrow { EntityNotFoundException() }
    }

    @Transactional
    fun save(entity: UserEntity): UserEntity {
        val entityId = entity.id
        if (entityId != null) {
            entity.passwordHash = entity.password
                ?.takeUnless { entity.password.isNullOrEmpty() }
                ?.let { passwordEncoder.encode(it) }
                ?: repository.loadPasswordHash(entityId)
        } else {
            entity.passwordHash = passwordEncoder.encode(entity.password)
        }

        transaction {
            afterCommit {
                val action = if (entityId == null) Action.CREATED else Action.UPDATED
                eventPublisher.emit(action, entity)
            }
        }
        return repository.save(entity)
    }

    @Transactional
    fun delete(id: UUID) {
        transaction {
            afterCommit {
                eventPublisher.emit(Action.DELETED, UserEntity().apply { this.id = id })
            }
        }
        repository.deleteById(id)
    }
}