package io.averkhoglyad.popug.auth.service

import io.averkhoglyad.popug.auth.service.core.WritableService
import io.averkhoglyad.popug.auth.entity.UserEntity
import io.averkhoglyad.popug.auth.repository.UserRepository
import io.averkhoglyad.popug.auth.util.transaction
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : WritableService<UserEntity, UUID> {

    @Transactional(readOnly = true)
    override fun findAll(): List<UserEntity> {
        return repository.findAll()
    }

    @Transactional(readOnly = true)
    override fun findList(pageable: Pageable): Page<UserEntity> {
        return repository.findAll(pageable)
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = true)
    override fun findEntity(id: UUID): UserEntity {
        return repository.findById(id)
            .orElseThrow { EntityNotFoundException() }
    }

    @Transactional
    override fun save(entity: UserEntity): UserEntity {
        var entityId = entity.id
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
                // TODO: Send event
            }
        }
        return repository.save(entity)
    }

    @Transactional
    override fun delete(id: UUID) {
        transaction {
            afterCommit {
                // TODO: Send event
            }
        }
        repository.deleteById(id)
    }
}