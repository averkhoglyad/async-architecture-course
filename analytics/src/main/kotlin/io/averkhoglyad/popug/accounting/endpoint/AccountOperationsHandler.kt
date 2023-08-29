package io.averkhoglyad.popug.accounting.endpoint

import io.averkhoglyad.popug.accounting.config.KAFKA_LISTENER_USER_ACCOUNT_OPERATIONS
import io.averkhoglyad.popug.accounting.core.event.business.TaskOperationDto
import io.averkhoglyad.popug.accounting.core.event.business.WithdrawOperationDto
import io.averkhoglyad.popug.accounting.core.service.UserAccountOperationService
import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.common.log4j
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
@KafkaListener(
    topics = ["UserAccountOperations"],
    idIsGroup = false,
    containerFactory = KAFKA_LISTENER_USER_ACCOUNT_OPERATIONS
)
class AccountOperationsHandler(
    private val operationService: UserAccountOperationService
) {

    private val log by log4j()

    @KafkaHandler
    fun handleTaskCreated(@Payload operation: TaskOperationDto,
                          @Header(name = PopugKafkaHeaders.EVENT_NAME, required = false, defaultValue = "") eventNameHeader: String) {
        log.debug("Received account operation event {}:\n{}", eventNameHeader, operation)
        operationService.save(operation)
    }

    @KafkaHandler
    fun handleTaskCreated(@Payload operation: WithdrawOperationDto,
                          @Header(name = PopugKafkaHeaders.EVENT_NAME, required = false, defaultValue = "") eventNameHeader: String) {
        log.debug("Received account operation event {}:\n{}", eventNameHeader, operation)
        operationService.save(operation)
    }

    @KafkaHandler(isDefault = true)
    fun handleUnknown(
        @Payload payload: Map<String, Any?>,
        @Header(name = PopugKafkaHeaders.EVENT_NAME, required = false, defaultValue = "") eventNameHeader: String
    ) {
        log.warn("Unexpected event `{}` with payload:\n{}", eventNameHeader, payload)
    }
}
