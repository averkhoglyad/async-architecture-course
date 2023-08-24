package io.averkhoglyad.popug.tasks.output

import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.common.log4j
import io.averkhoglyad.popug.tasks.persistence.entity.Task
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

typealias TaskLifecycleEvent = Pair<TaskLifecycle, Task>

@Component
class TaskLifecycleEventPublisher(
    private val streamBridge: StreamBridge
) : EventPublisher<TaskLifecycleEvent> {

    private val logger by log4j()

    private val bindingName = "taskLifecycle"

    override fun emit(event: TaskLifecycleEvent) {
        val (eventName, task) = event
        val dto = event.toDto()

        logger.debug("Sending lifecycle message {} for Task#{}: {}", eventName, task.publicId, dto)

        val headers = mapOf(
            PopugKafkaHeaders.EVENT_NAME to eventName.toString(),
            PopugKafkaHeaders.EVENT_VERSION to "v1",
            PopugKafkaHeaders.PUBLISHED_AT to Instant.now().toString(),
        )
        streamBridge.send(bindingName, GenericMessage(dto, headers))
    }
}

fun EventPublisher<TaskLifecycleEvent>.created(task: Task) = this.emit(TaskLifecycle.CREATED to task)
fun EventPublisher<TaskLifecycleEvent>.reassigned(task: Task) = this.emit(TaskLifecycle.REASSIGNED to task)
fun EventPublisher<TaskLifecycleEvent>.closed(task: Task) = this.emit(TaskLifecycle.CLOSED to task)

enum class TaskLifecycle {
    CREATED,
    REASSIGNED,
    CLOSED,
}

sealed class TaskLifecycleDto {
    data class TaskCreatedDto(
        val taskPublicId: UUID,
        val userPublicId: UUID,
        val cost: Int,
    ) : TaskLifecycleDto()

    data class TaskReassignDto(
        val taskPublicId: UUID,
        val userPublicId: UUID,
        val cost: Int,
    ) : TaskLifecycleDto()

    data class TaskClosedDto(
        val taskPublicId: UUID,
        val userPublicId: UUID,
        val revenue: Int,
    ) : TaskLifecycleDto()
}

fun TaskLifecycleEvent.toDto(): TaskLifecycleDto = when (first) {
    TaskLifecycle.CREATED -> TaskLifecycleDto.TaskCreatedDto(
        taskPublicId = this.second.id!!,
        userPublicId = this.second.assignee!!.id!!,
        cost = this.second.cost,
    )

    TaskLifecycle.REASSIGNED -> TaskLifecycleDto.TaskReassignDto(
        taskPublicId = this.second.id!!,
        userPublicId = this.second.assignee!!.id!!,
        cost = this.second.cost,
    )

    TaskLifecycle.CLOSED -> TaskLifecycleDto.TaskClosedDto(
        taskPublicId = this.second.id!!,
        userPublicId = this.second.assignee!!.id!!,
        revenue = this.second.revenue
    )
}
