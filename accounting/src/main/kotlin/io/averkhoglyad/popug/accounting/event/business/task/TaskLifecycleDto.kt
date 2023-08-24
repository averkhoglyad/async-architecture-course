package io.averkhoglyad.popug.accounting.event.business.task

import java.util.UUID

sealed class TaskLifecycleDto

data class TaskCreated(
    val taskPublicId: UUID,
    val userPublicId: UUID,
    val cost: Int,
) : TaskLifecycleDto()

data class TaskReassigned(
    val taskPublicId: UUID,
    val userPublicId: UUID,
    val cost: Int,
) : TaskLifecycleDto()

data class TaskClosed(
    val taskPublicId: UUID,
    val userPublicId: UUID,
    val revenue: Int,
) : TaskLifecycleDto()

enum class TaskLifecycleEvent {
    CREATED,
    REASSIGNED,
    CLOSED,
    UNKNOWN;
    companion object {
        fun parse(value: String): TaskLifecycleEvent? {
            return try {
                TaskLifecycleEvent.valueOf(value)
            } catch (e: Exception) {
                UNKNOWN
            }
        }
    }
}