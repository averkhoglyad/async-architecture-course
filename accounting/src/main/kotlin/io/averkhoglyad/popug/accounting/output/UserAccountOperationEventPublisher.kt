package io.averkhoglyad.popug.accounting.output

import io.averkhoglyad.popug.accounting.persistence.entity.UserAccountOperation
import io.averkhoglyad.popug.accounting.persistence.entity.UserAccountTaskOperation
import io.averkhoglyad.popug.accounting.persistence.entity.UserAccountWithdrawOperation
import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.common.kafka.addAsString
import io.averkhoglyad.popug.common.log4j
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

private typealias AccountOperationEvent = Pair<AccountEvents, UserAccountOperation>

private const val topic = "UserAccountOperations"

@Component
class UserAccountOperationEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : EventPublisher<AccountOperationEvent> {

    private val logger by log4j()

    override fun emit(event: AccountOperationEvent) {
        val (eventName, operation) = event
        val dto = when(operation) {
            is UserAccountTaskOperation -> operation.toDto()
            is UserAccountWithdrawOperation -> operation.toDto()
            else -> throw IllegalStateException()
        }

        logger.debug("Sending streaming message {} for UserAccountOperation#{}", eventName, operation.publicId, dto)

        val record = ProducerRecord<String, Any>(topic, dto)
        record.headers().addAsString(PopugKafkaHeaders.EVENT_NAME, eventName)
        record.headers().addAsString(PopugKafkaHeaders.EVENT_VERSION, "v1")
        record.headers().addAsString(PopugKafkaHeaders.PUBLISHED_AT, Instant.now())
        kafkaTemplate.send(record)
    }
}

fun EventPublisher<AccountOperationEvent>.costPaid(operation: UserAccountOperation) = this.emit(AccountEvents.COST_PAID to operation)
fun EventPublisher<AccountOperationEvent>.revenuePaid(operation: UserAccountOperation) = this.emit(AccountEvents.REVENUE_PAID to operation)
fun EventPublisher<AccountOperationEvent>.balanceWithdrawn(operation: UserAccountOperation) = this.emit(AccountEvents.BALANCE_WITHDRAWN to operation)

enum class AccountEvents {
    COST_PAID,
    REVENUE_PAID,
    BALANCE_WITHDRAWN
}

data class TaskOperationDto(
    val operationPublicId: UUID,
    val userPublicId: UUID,
    val accountPublicId: UUID,
    val taskPublicId: UUID,
    val amount: Int = 0,
    val occurredAt: Instant
)

data class WithdrawOperationDto(
    val operationPublicId: UUID,
    val userPublicId: UUID,
    val accountPublicId: UUID,
    val amount: Int = 0,
    val occurredAt: Instant
)

private fun UserAccountWithdrawOperation.toDto(): WithdrawOperationDto = WithdrawOperationDto(
    operationPublicId = this.publicId,
    accountPublicId = this.userAccount.publicId,
    userPublicId = this.userAccount.owner.publicId,
    amount = this.amount,
    occurredAt = this.occurredAt
)

private fun UserAccountTaskOperation.toDto(): TaskOperationDto = TaskOperationDto(
    operationPublicId = this.publicId,
    accountPublicId = this.userAccount.publicId,
    userPublicId = this.userAccount.owner.publicId,
    taskPublicId = this.task!!.publicId,
    amount = this.amount,
    occurredAt = this.occurredAt
)
