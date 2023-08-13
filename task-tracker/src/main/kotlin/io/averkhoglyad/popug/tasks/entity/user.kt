package io.averkhoglyad.popug.tasks.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "\"user\"")
class User {
    @Id
    var id: UUID? = null
    var login: String = ""
    var name: String = ""

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.NONE
}

enum class UserRole {
    ADMIN,
    MANAGER,
    USER,
    NONE
}
