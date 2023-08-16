package io.averkhoglyad.popug.tasks.output

import io.averkhoglyad.popug.tasks.persistence.entity.Task
import io.averkhoglyad.popug.tasks.util.log4j
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Component

private typealias TaskStreamingEvent = Pair<StreamingAction, Task>

@Component
class TaskStreamingEventPublisher(
    private val streamBridge: StreamBridge
) : EventPublisher<TaskStreamingEvent> {

    private val logger by log4j()

    private val bindingName = "Streaming-Task"

    override fun emit(event: TaskStreamingEvent) {
        val (action, task) = event
        val dto = event.toDto()
        logger.debug("Sending streaming message {} for Task#{}: {}", action, task, dto)
        streamBridge.send(bindingName, dto)
    }
}

fun EventPublisher<TaskStreamingEvent>.emit(action: StreamingAction, task: Task) = this.emit(action to task)

data class TaskDto(
    val action: StreamingAction,
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var userCost: Int = 0,
    var userRevenue: Int = 0,
    var status: String = ""
)

private fun TaskStreamingEvent.toDto(): TaskDto = TaskDto(
    action = first,
    id = second.id.toString(),
    title = second.title,
    description = second.description,
    userCost = second.userCost,
    userRevenue = second.userRevenue,
    status = second.status.name,
)

enum class StreamingAction {
    CREATED,
    UPDATED,
    DELETED
}