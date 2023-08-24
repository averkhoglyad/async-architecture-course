package io.averkhoglyad.popug.accounting.service

import io.averkhoglyad.popug.accounting.event.TaskOperationDto
import io.averkhoglyad.popug.accounting.event.WithdrawOperationDto
import io.averkhoglyad.popug.accounting.persistence.entity.*
import io.averkhoglyad.popug.accounting.persistence.repository.TaskRepository
import io.averkhoglyad.popug.accounting.persistence.repository.UserAccountOperationRepository
import io.averkhoglyad.popug.accounting.persistence.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserAccountOperationService(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val accountOperationRepository: UserAccountOperationRepository,
) {

    @Transactional
    fun save(operation: TaskOperationDto) {
        accountOperationRepository.save(operation.toEntity())
    }

    @Transactional
    fun save(operation: WithdrawOperationDto) {
        accountOperationRepository.save(operation.toEntity())
    }

    private fun TaskOperationDto.toEntity(): UserAccountTaskOperation {
        return UserAccountTaskOperation().also {
            it.publicId = this.operationPublicId
            it.amount = this.amount
            it.user = resolveUserByPublicId(this.userPublicId)
            it.task = resolveTaskByPublicId(this.taskPublicId)
            it.occurredAt = this.occurredAt
        }
    }

    private fun WithdrawOperationDto.toEntity(): UserAccountWithdrawOperation {
        return UserAccountWithdrawOperation().also {
            it.publicId = this.operationPublicId
            it.amount = this.amount
            it.user = resolveUserByPublicId(this.userPublicId)
            it.occurredAt = this.occurredAt
        }
    }

    private fun resolveUserByPublicId(userId: UUID): UserEntity {
        return userRepository.findByPublicId(userId)
            .orElseGet { userRepository.save(UserEntity().apply { publicId = userId }) }
    }

    private fun resolveTaskByPublicId(taskId: UUID): Task {
        return taskRepository.findByPublicId(taskId)
            .orElseGet { taskRepository.save(Task().apply { publicId = taskId }) }
    }
}

