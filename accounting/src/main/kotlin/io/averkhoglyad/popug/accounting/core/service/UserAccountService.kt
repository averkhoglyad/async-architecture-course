package io.averkhoglyad.popug.accounting.core.service

import io.averkhoglyad.popug.accounting.core.persistence.entity.*
import io.averkhoglyad.popug.accounting.core.persistence.repository.TaskRepository
import io.averkhoglyad.popug.accounting.core.persistence.repository.UserAccountOperationRepository
import io.averkhoglyad.popug.accounting.core.persistence.repository.UserAccountRepository
import io.averkhoglyad.popug.accounting.core.persistence.repository.UserRepository
import io.averkhoglyad.popug.accounting.core.service.notification.NotificationSender
import io.averkhoglyad.popug.accounting.core.service.payment.PaymentGateway
import io.averkhoglyad.popug.accounting.output.UserAccountOperationEventPublisher
import io.averkhoglyad.popug.accounting.output.balanceWithdrawn
import io.averkhoglyad.popug.accounting.output.costPaid
import io.averkhoglyad.popug.accounting.output.revenuePaid
import io.averkhoglyad.popug.common.transaction.transaction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserAccountService(
    private val accountRepository: UserAccountRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val accountOperationRepository: UserAccountOperationRepository,
    private val paymentGateway: PaymentGateway,
    private val notificationSender: NotificationSender,
    private val operationEventPublisher: UserAccountOperationEventPublisher
) {

    @Transactional
    fun withdrawCostForTask(taskId: UUID, userId: UUID, userCost: Int) {
        val operation = fundTransfer(taskId, userId, -userCost)
        transaction {
            afterCommit {
                operationEventPublisher.costPaid(operation)
            }
        }
    }

    @Transactional
    fun fundRevenueForTask(taskId: UUID, userId: UUID, userRevenue: Int) {
        val operation = fundTransfer(taskId, userId, userRevenue)
        transaction {
            afterCommit {
                operationEventPublisher.revenuePaid(operation)
            }
        }
    }

    private fun fundTransfer(taskId: UUID, userId: UUID, amount: Int): UserAccountOperation {
        val user = userRepository.findByPublicId(userId)
            .orElseGet {
                userRepository.save(UserEntity().apply { publicId = userId })
            }
        val account = accountRepository.findByOwner(user)
            .orElseGet {
                accountRepository.save(UserAccount().apply {
                    publicId = UUID.randomUUID()
                    owner = user
                })
            }

        val task = taskRepository.findByPublicId(taskId)
            .orElseGet {
                taskRepository.save(Task().apply { publicId = taskId })
            }
        val operation = UserAccountTaskOperation()
            .apply {
                this.task = task
                this.userAccount = account
                this.amount = amount
            }
        accountOperationRepository.save(operation)

        accountRepository.fundTransfer(account, amount)

        return operation
    }

    @Transactional
    fun withdrawOutFromBalance(account: UserAccount) {
        val amount = account.balance

        require(amount > 0)

        val operation = UserAccountWithdrawOperation()
            .apply {
                this.userAccount = account
                this.amount = -amount
            }
        accountOperationRepository.save(operation)

        accountRepository.fundTransfer(account, -amount)

        paymentGateway.payOutFromAccount(account, amount)

        transaction {
            afterCommit {
                operationEventPublisher.balanceWithdrawn(operation)
                notificationSender.send(account.owner, "Sent \$$amount")
            }
        }
    }
}