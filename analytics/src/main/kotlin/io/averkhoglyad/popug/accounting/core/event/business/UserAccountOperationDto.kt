package io.averkhoglyad.popug.accounting.core.event.business

import java.time.Instant
import java.util.*

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

enum class AccountOperationEvents {
    COST_PAID,
    REVENUE_PAID,
    BALANCE_WITHDRAWN;

    companion object {
        fun parse(value: String): AccountOperationEvents? {
            return try {
                AccountOperationEvents.valueOf(value)
            } catch (e: Exception) {
                null
            }
        }
    }
}
