package io.averkhoglyad.popug.auth.core.repository

import io.averkhoglyad.popug.auth.core.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserRepository : JpaRepository<UserEntity, UUID> {

    fun findByLogin(login: String): Optional<UserEntity>

    @Query("SELECT u.passwordHash FROM UserEntity u WHERE u.id=?1")
    fun loadPasswordHash(userId: UUID): String

}