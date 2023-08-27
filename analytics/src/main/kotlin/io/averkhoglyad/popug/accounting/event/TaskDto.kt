package io.averkhoglyad.popug.accounting.event

import java.util.*

data class TaskDto(
    val publicId: UUID,
    val title: String,
    val description: String,
)
