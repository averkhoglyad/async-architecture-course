package io.averkhoglyad.popug.tasks.core.persistence.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "\"user\"")
class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, insertable = false)
    lateinit var id: UUID

    @Column(updatable = false)
    lateinit var publicId: UUID

    var name: String = ""

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.NONE

    var isActive: Boolean = true
}

enum class UserRole {
    ADMIN,
    MANAGER,
    USER,
    NONE,
    UNKNOWN;

    companion object {
        fun parse(value: String): UserRole {
            return try {
                UserRole.valueOf(value)
            } catch (e: Exception) {
                UNKNOWN
            }
        }
    }
}
