package io.averkhoglyad.popug.tasks.output

import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.common.log4j
import io.averkhoglyad.popug.tasks.event.CudEvent
import io.averkhoglyad.popug.tasks.persistence.entity.Task
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component
import java.time.Instant
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
        val headers = mapOf(
            PopugKafkaHeaders.EVENT_NAME to eventName.toString(),
            PopugKafkaHeaders.EVENT_VERSION to "v1",
            PopugKafkaHeaders.PUBLISHED_AT to Instant.now().toString(),
        )
        streamBridge.send(bindingName, GenericMessage(dto, headers))
    }
}

fun EventPublisher<TaskStreamingEvent>.created(user: Task) = this.emit(CudEvent.CREATED to user)
fun EventPublisher<TaskStreamingEvent>.updated(user: Task) = this.emit(CudEvent.UPDATED to user)
fun EventPublisher<TaskStreamingEvent>.deleted(user: Task) = this.emit(CudEvent.DELETED to user)

data class TaskDto(
    var publicId: UUID,
    var title: String = "",
    var description: String = ""
)

private fun Task.toDto(): TaskDto = TaskDto(
    publicId = this.publicId,
    title = this.title,
    description = this.description
)
