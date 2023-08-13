package io.averkhoglyad.popug.tasks.service

import io.averkhoglyad.popug.tasks.entity.Task
import io.averkhoglyad.popug.tasks.entity.TaskStatus
import io.averkhoglyad.popug.tasks.repository.TaskRepository
import io.averkhoglyad.popug.tasks.security.SecurityService
import io.averkhoglyad.popug.tasks.service.accounting.CostsRevenueGenerator
import io.averkhoglyad.popug.tasks.service.assignee.AssigneeGenerator
import io.averkhoglyad.popug.tasks.util.transaction
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserTaskService(
    private val securityService: SecurityService,
    private val taskRepository: TaskRepository,
    private val costRevenueGenerator: CostsRevenueGenerator,
    private val assigneeGenerator: AssigneeGenerator
) {

    @Transactional(readOnly = true)
    fun findList(pageable: Pageable): Page<Task> {
        return taskRepository.findAll(pageable)
    }

    @Transactional(readOnly = true)
    fun findOwnList(pageable: Pageable): Page<Task> {
        val currentUserId = securityService.currentUserId()
        requireNotNull(currentUserId)
        return taskRepository.findByAssigneeId(currentUserId, pageable)
    }

    @Transactional(readOnly = true)
    @Throws(EntityNotFoundException::class)
    fun findEntity(id: UUID): Task {
        return taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException() }
    }

    @Transactional
    fun create(task: Task): Task {
        task.assignee = assigneeGenerator.assignee()
        task.userCost = costRevenueGenerator.generateCost(task)
        task.userRevenue = costRevenueGenerator.generateRevenue(task)

        transaction {
            afterCommit {
                // TODO: Send event
            }
        }
        return taskRepository.save(task)
    }

    @Transactional
    @Throws(EntityNotFoundException::class)
    fun close(id: UUID): Task {
        val task = taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException() }
        require(task.assignee?.id == securityService.currentUserId())
        task.status = TaskStatus.CLOSED
        transaction {
            afterCommit {
                // TODO: Send event
            }
        }
        return taskRepository.save(task)
    }

    @Transactional
    fun reassign() {
        taskRepository.streamByStatus(TaskStatus.OPEN)
            .peek { it.assignee = assigneeGenerator.assignee() }
            .forEach { taskRepository.save(it) }

        transaction {
            afterCommit {
                // TODO: Send event
            }
        }
    }
}