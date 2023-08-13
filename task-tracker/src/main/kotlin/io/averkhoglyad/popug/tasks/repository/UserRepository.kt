package io.averkhoglyad.popug.tasks.repository

import io.averkhoglyad.popug.tasks.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {

    @Query("FROM User u ORDER BY random() LIMIT 1")
    fun randomUser(): Optional<User>

}
