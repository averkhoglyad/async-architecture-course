package io.averkhoglyad.popug.auth.repository

import io.averkhoglyad.popug.auth.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {

    fun findByLogin(login: String): Optional<User>

    @Query("SELECT u.passwordHash FROM User u WHERE u.id=?1")
    fun loadPasswordHash(userId: UUID): String

}