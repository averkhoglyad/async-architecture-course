package io.averkhoglyad.popug.auth.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.persistence.EnumType.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import java.util.UUID
import kotlin.jvm.Transient

@Entity
@Table(name = "\"user\"")
class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, insertable = false)
    var id: UUID? = null

    @Column(updatable = false)
    lateinit var publicId: UUID

    @NotBlank
    var login: String = ""

    @JsonIgnore
    @NotEmpty
    var passwordHash: String = ""

    @NotBlank
    var name: String = ""

    @Enumerated(STRING)
    var role: UserRole = UserRole.NONE

    @Transient
    var password: String? = null
}

enum class UserRole {
    ADMIN,
    MANAGER,
    USER,
    NONE
}