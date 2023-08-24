package io.averkhoglyad.popug.accounting.service

import io.averkhoglyad.popug.accounting.event.UserDto
import io.averkhoglyad.popug.accounting.persistence.entity.UserAccount
import io.averkhoglyad.popug.accounting.persistence.entity.UserEntity
import io.averkhoglyad.popug.accounting.persistence.entity.UserRole
import io.averkhoglyad.popug.accounting.persistence.repository.UserAccountRepository
import io.averkhoglyad.popug.accounting.persistence.repository.UserRepository
import io.averkhoglyad.popug.common.log4j
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val accountRepository: UserAccountRepository
) {

    private val log by log4j()

    @Transactional
    fun create(dto: UserDto) {
        if (userRepository.findByPublicId(dto.publicId).isPresent) {
            log.warn("Not unique User#{}", dto.publicId)
            return
        }
        val user = userRepository
            .save(dto.toEntity(UserEntity()))
        val account = UserAccount()
            .apply {
                publicId = UUID.randomUUID()
                owner = user
            }
        accountRepository.save(account)
    }

    @Transactional
    fun update(dto: UserDto) {
        userRepository
            .findByPublicId(dto.publicId)
            .ifPresent { userRepository.save(dto.toEntity(it)) }
    }

    @Transactional
    fun delete(userPublicId: UUID) {
        userRepository
            .findByPublicId(userPublicId)
            .ifPresent { userRepository.save(it.apply { isActive = false }) }
    }
}

fun UserDto.toEntity(): UserEntity = toEntity(UserEntity())

fun UserDto.toEntity(entity: UserEntity): UserEntity = entity
    .also {
        it.publicId = this.publicId
        it.name = this.name
        it.role = UserRole.parse(this.role)
    }
