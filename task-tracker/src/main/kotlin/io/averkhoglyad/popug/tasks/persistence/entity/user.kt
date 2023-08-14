package io.averkhoglyad.popug.tasks.persistence.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "\"user\"")
class UserEntity {
    @Id
    var id: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
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
