package io.averkhoglyad.popug.tasks.persistence.repository

import io.averkhoglyad.popug.tasks.persistence.entity.UserEntity
import io.averkhoglyad.popug.tasks.persistence.entity.UserRole
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import java.util.stream.Stream

interface UserRepository : JpaRepository<UserEntity, UUID> {

    fun countByRole(role: UserRole): Long
    fun findByRole(role: UserRole, position: Pageable): List<UserEntity>

}
