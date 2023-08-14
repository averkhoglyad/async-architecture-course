package io.averkhoglyad.popug.tasks.persistence.repository

import io.averkhoglyad.popug.tasks.persistence.entity.User
import io.averkhoglyad.popug.tasks.persistence.entity.UserRole
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Window
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import java.util.stream.Stream

interface UserRepository : JpaRepository<User, UUID> {

//    @Query("FROM User u ORDER BY random() LIMIT 1")
//    fun randomUser(): Optional<User>
//    @Query("FROM User u WHERE u.role=?1 ORDER BY random() LIMIT 1")
//    fun randomUserByRole(role: UserRole): Optional<User>

    fun countByRole(role: UserRole): Long
    fun findFirstByRole(role: UserRole, position: Pageable): List<User>

    fun findByRole(role: UserRole): List<User>
    fun streamByRole(role: UserRole): Stream<User>

}
