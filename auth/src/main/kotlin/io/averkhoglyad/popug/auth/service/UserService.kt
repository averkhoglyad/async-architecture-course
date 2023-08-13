package io.averkhoglyad.popug.auth.service

import io.averkhoglyad.popug.auth.service.core.WritableService
import io.averkhoglyad.popug.auth.entity.User
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
    private val repo: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : WritableService<User, UUID> {

    @Transactional(readOnly = true)
    override fun findAll(): List<User> {
        return repo.findAll()
    }

    @Transactional(readOnly = true)
    override fun findList(pageable: Pageable): Page<User> {
        return repo.findAll(pageable)
    }

    @Throws(EntityNotFoundException::class)
    @Transactional(readOnly = true)
    override fun findEntity(id: UUID): User {
        return repo.findById(id)
            .orElseThrow { EntityNotFoundException() }
    }

    @Transactional
    override fun save(entity: User): User {
        var entityId = entity.id
        if (entityId != null) {
            entity.passwordHash = entity.password
                ?.takeUnless { entity.password.isNullOrEmpty() }
                ?.let { passwordEncoder.encode(it) }
                ?: repo.loadPasswordHash(entityId)
        } else {
            entity.passwordHash = passwordEncoder.encode(entity.password)
        }

        transaction {
            afterCommit {
                // TODO: Send event
            }
        }
        return repo.save(entity)
    }

    @Transactional
    override fun delete(id: UUID) {
        transaction {
            afterCommit {
                // TODO: Send event
            }
        }
        repo.deleteById(id)
    }
}