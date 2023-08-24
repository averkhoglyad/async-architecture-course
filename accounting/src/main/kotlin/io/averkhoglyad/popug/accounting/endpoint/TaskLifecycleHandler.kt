package io.averkhoglyad.popug.accounting.endpoint

import io.averkhoglyad.popug.accounting.config.KAFKA_LISTENER_TASK_LIFECYCLE
import io.averkhoglyad.popug.accounting.event.business.task.TaskClosed
import io.averkhoglyad.popug.accounting.event.business.task.TaskCreated
import io.averkhoglyad.popug.accounting.event.business.task.TaskReassigned
import io.averkhoglyad.popug.accounting.service.UserAccountService
import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.common.log4j
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
@KafkaListener(
    topics = ["TaskLifecycle"],
    idIsGroup = false,
    containerFactory = KAFKA_LISTENER_TASK_LIFECYCLE
)
class TaskLifecycleHandler(
    private val service: UserAccountService
) {

    private val log by log4j()

    @KafkaHandler
    fun handleTaskCreated(task: TaskCreated) {
        service.withdrawCostForTask(task.taskPublicId, task.userPublicId, task.cost)
    }

    @KafkaHandler
    fun handleTaskReassigned(task: TaskReassigned) {
        service.withdrawCostForTask(task.taskPublicId, task.userPublicId, task.cost)
    }

    @KafkaHandler
    fun handleTaskClosed(task: TaskClosed) {
        service.fundRevenueForTask(task.taskPublicId, task.userPublicId, task.revenue)
    }

    @KafkaHandler(isDefault = true)
    fun handleUnknown(
        @Payload payload: Map<String, Any?>,
        @Header(name = PopugKafkaHeaders.EVENT_NAME, required = false, defaultValue = "") eventNameHeader: String
    ) {
        log.warn("Unexpected event `{}` with payload:\n{}", eventNameHeader, payload)
    }
}
