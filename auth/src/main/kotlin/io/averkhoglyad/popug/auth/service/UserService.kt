package io.averkhoglyad.popug.auth.service

import io.averkhoglyad.popug.auth.entity.UserEntity
import io.averkhoglyad.popug.auth.output.*
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
            entity.publicId = UUID.randomUUID()
            entity.passwordHash = passwordEncoder.encode(entity.password)
        }

        transaction {
            afterCommit {
                if (entityId == null) {
                    eventPublisher.created(entity)
                } else {
                    eventPublisher.updated(entity)
                }
            }
        }
        return repository.save(entity)
    }

    @Transactional
    fun delete(id: UUID) {
        val user = repository.findById(id)
            .orElseThrow { EntityNotFoundException() }
        repository.delete(user)
        transaction {
            afterCommit {
                eventPublisher.deleted(user)
            }
        }
    }
}