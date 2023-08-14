package io.averkhoglyad.popug.tasks.event

data class UserDto(
    val action: Action,
    val id: String,
    val login: String,
    val name: String,
    val role: String
)


enum class Action {
    CREATE,
    UPDATE,
    DELETE
}
