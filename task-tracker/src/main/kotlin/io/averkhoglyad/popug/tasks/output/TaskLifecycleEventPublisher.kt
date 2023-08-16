package io.averkhoglyad.popug.tasks.output

import io.averkhoglyad.popug.tasks.persistence.entity.Task
import io.averkhoglyad.popug.tasks.util.log4j
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Component

typealias TaskLifecycleEvent = Pair<TaskLifecycle, Task>

@Component
class TaskLifecycleEventPublisher(
    private val streamBridge: StreamBridge
) : EventPublisher<TaskLifecycleEvent> {

    private val logger by log4j()

    private val bindingName = "taskLifecycle"

    override fun emit(event: TaskLifecycleEvent) {
        val (action, task) = event
        val dto = event.toDto()
        logger.debug("Sending lifecycle message {} for Task#{}: {}", action, task.id, dto)
        streamBridge.send(bindingName, dto)
    }
}

fun EventPublisher<TaskLifecycleEvent>.emit(action: TaskLifecycle, task: Task) = this.emit(action to task)

enum class TaskLifecycle {
    CREATED,
    REASSIGNED,
    CLOSED,
}

sealed class TaskLifecycleDto {
    data class TaskCreatedDto(
        var taskId: String = "",
        var userId: String = "",
        var userCost: Int = 0,
        var userRevenue: Int = 0,
    ) : TaskLifecycleDto()

    data class TaskReassignDto(
        var taskId: String = "",
        var userId: String = "",
    ) : TaskLifecycleDto()

    data class TaskClosedDto(
        var taskId: String = "",
    ) : TaskLifecycleDto()
}

fun TaskLifecycleEvent.toDto(): TaskLifecycleDto = when (first) {
    TaskLifecycle.CREATED -> TaskLifecycleDto.TaskCreatedDto(
        taskId = this.second.id.toString(),
        userId = this.second.assignee?.id.toString(),
        userCost = this.second.userCost,
        userRevenue = this.second.userRevenue,
    )

    TaskLifecycle.REASSIGNED -> TaskLifecycleDto.TaskReassignDto(
        taskId = this.second.id.toString(),
        userId = this.second.assignee?.id.toString(),
    )

    TaskLifecycle.CLOSED -> TaskLifecycleDto.TaskClosedDto(
        taskId = this.second.id.toString()
    )
}
