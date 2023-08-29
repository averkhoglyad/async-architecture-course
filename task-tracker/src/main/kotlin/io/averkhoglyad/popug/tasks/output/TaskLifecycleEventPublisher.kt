package io.averkhoglyad.popug.tasks.output

import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.common.kafka.addAsString
import io.averkhoglyad.popug.common.log4j
import io.averkhoglyad.popug.tasks.core.persistence.entity.Task
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

typealias TaskLifecycleEvent = Pair<TaskLifecycle, Task>

@Component
class TaskLifecycleEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : EventPublisher<TaskLifecycleEvent> {

    private val logger by log4j()

    private val topic = "TaskLifecycle"

    override fun emit(event: TaskLifecycleEvent) {
        val (eventName, task) = event
        val dto = event.toDto()

        logger.debug("Sending lifecycle message {} for Task#{}: {}", eventName, task.publicId, dto)

        val record = ProducerRecord<String, Any>(topic, dto)
        record.headers().addAsString(PopugKafkaHeaders.EVENT_NAME, eventName)
        record.headers().addAsString(PopugKafkaHeaders.EVENT_VERSION, "v1")
        record.headers().addAsString(PopugKafkaHeaders.PUBLISHED_AT, Instant.now())
        kafkaTemplate.send(record)
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
