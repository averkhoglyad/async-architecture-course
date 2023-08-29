package io.averkhoglyad.popug.accounting.core.event.streaming

import java.util.*

data class TaskDto(
    val publicId: UUID,
    val title: String,
    val description: String,
)
