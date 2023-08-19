package io.averkhoglyad.popug.tasks.output

import io.averkhoglyad.popug.tasks.event.CudEvent
import io.averkhoglyad.popug.tasks.persistence.entity.Task
import io.averkhoglyad.popug.tasks.util.log4j
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component
import java.util.UUID

private typealias TaskStreamingEvent = Pair<CudEvent, Task>

@Component
class TaskStreamingEventPublisher(
    private val streamBridge: StreamBridge
) : EventPublisher<TaskStreamingEvent> {

    private val logger by log4j()

    private val bindingName = "Streaming-Task"

    override fun emit(event: TaskStreamingEvent) {
        val (eventName, task) = event
        val dto = task.toDto()
        logger.debug("Sending streaming message {} for Task#{}: {}", eventName, task.publicId, dto)
        streamBridge.send(bindingName, GenericMessage(dto, mapOf("X-Event-Name" to eventName.toString())))
    }
}

fun EventPublisher<TaskStreamingEvent>.created(user: Task) = this.emit(CudEvent.CREATED to user)
fun EventPublisher<TaskStreamingEvent>.updated(user: Task) = this.emit(CudEvent.UPDATED to user)
fun EventPublisher<TaskStreamingEvent>.deleted(user: Task) = this.emit(CudEvent.DELETED to user)

data class TaskDto(
    var publicId: UUID,
    var title: String = "",
    var description: String = "",
    var userCost: Int = 0,
    var userRevenue: Int = 0,
    var status: String = ""
)

private fun Task.toDto(): TaskDto = TaskDto(
    publicId = this.publicId,
    title = this.title,
    description = this.description,
    userCost = this.userCost,
    userRevenue = this.userRevenue,
    status = this.status.name,
)
