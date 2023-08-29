package io.averkhoglyad.popug.accounting.core.event.streaming.task

import java.util.*

data class TaskDto(
    val publicId: UUID,
    val jiraId: String?,
    val title: String,
    val description: String,
)
