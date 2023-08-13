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
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null
    @NotBlank
    var login: String = ""
    @JsonIgnore
    @NotEmpty
    var passwordHash: String = ""
    @NotBlank
    var name: String = ""
    @Enumerated(STRING)
    var role: Role = Role.NONE
    @Transient
    var password: String? = null
}

enum class Role {
    ADMIN,
    MANAGER,
    USER,
    NONE
}