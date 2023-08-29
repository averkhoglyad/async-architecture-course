package io.averkhoglyad.popug.tasks.core.event

import java.util.*

data class UserDto(
    val publicId: UUID,
    val name: String,
    val role: String
)
