package io.averkhoglyad.popug.tasks.core.persistence.repository

import io.averkhoglyad.popug.tasks.core.persistence.entity.UserEntity
import io.averkhoglyad.popug.tasks.core.persistence.entity.UserRole
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*
import java.util.stream.Stream

interface UserRepository : JpaRepository<UserEntity, UUID> {

    fun findByPublicId(publicId: UUID): Optional<UserEntity>

    @Query("SELECT count(u) FROM UserEntity u WHERE u.isActive=true AND u.role=?1")
    fun countByRole(role: UserRole): Long

    @Query("FROM UserEntity u WHERE u.isActive=true AND u.role=?1")
    fun findByRole(role: UserRole, position: Pageable): List<UserEntity>

}
