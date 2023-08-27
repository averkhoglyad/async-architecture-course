package io.averkhoglyad.popug.accounting.event.streaming.user

import java.util.*

data class UserDto(
    val publicId: UUID,
    val name: String,
    val role: String
)
