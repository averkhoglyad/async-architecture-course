package io.averkhoglyad.popug.auth.repository

import io.averkhoglyad.popug.auth.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {

    fun findByLogin(login: String): Optional<UserEntity>

    @Query("SELECT u.passwordHash FROM UserEntity u WHERE u.id=?1")
    fun loadPasswordHash(userId: UUID): String

}