package io.averkhoglyad.popug.tasks.output

import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.common.kafka.addAsString
import io.averkhoglyad.popug.common.log4j
import io.averkhoglyad.popug.tasks.core.event.CudEvent
import io.averkhoglyad.popug.tasks.core.persistence.entity.Task
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

private typealias TaskStreamingEvent = Pair<CudEvent, Task>

@Component
class TaskStreamingEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : EventPublisher<TaskStreamingEvent> {

    private val logger by log4j()

    private val topic = "Streaming-Task"

    override fun emit(event: TaskStreamingEvent) {
        val (eventName, task) = event
        val dto = task.toDto()
        logger.debug("Sending streaming message {} for Task#{}: {}", eventName, task.publicId, dto)

        val record = ProducerRecord<String, Any>(topic, dto.publicId.toString(), dto)
        record.headers().addAsString(PopugKafkaHeaders.EVENT_NAME, eventName)
        record.headers().addAsString(PopugKafkaHeaders.EVENT_VERSION, "v1")
        record.headers().addAsString(PopugKafkaHeaders.PUBLISHED_AT, Instant.now())
        kafkaTemplate.send(record)
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
    title = "${this.jiraId} - ${this.title}",
    description = this.description
)
