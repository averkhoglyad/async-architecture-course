package io.averkhoglyad.popug.tasks.persistence.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "\"user\"")
class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, insertable = false)
    var id: UUID? = null
    @Column(updatable = false)
    var publicId: UUID? = null
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
