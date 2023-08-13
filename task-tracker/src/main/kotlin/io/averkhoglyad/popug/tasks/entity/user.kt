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
    var role: Role = Role.NONE
}

enum class Role {
    ADMIN,
    MANAGER,
    USER,
    NONE
}
