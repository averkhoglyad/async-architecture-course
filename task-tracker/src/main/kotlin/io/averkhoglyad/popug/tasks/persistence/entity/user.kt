package io.averkhoglyad.popug.tasks.persistence.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "\"user\"")
class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    var publicId: String = ""

    var name: String = ""

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.NONE

    var isActive: Boolean = true
}

enum class UserRole {
    ADMIN,
    MANAGER,
    USER,
    NONE
}
