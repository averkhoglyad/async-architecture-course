package io.averkhoglyad.popug.tasks.core.service

import io.averkhoglyad.popug.common.security.SecurityService
import io.averkhoglyad.popug.common.transaction.transaction
import io.averkhoglyad.popug.tasks.output.*
import io.averkhoglyad.popug.tasks.core.persistence.entity.Task
import io.averkhoglyad.popug.tasks.core.persistence.entity.TaskStatus
import io.averkhoglyad.popug.tasks.core.persistence.repository.TaskRepository
import io.averkhoglyad.popug.tasks.core.service.accounting.CostsRevenueGenerator
import io.averkhoglyad.popug.tasks.core.service.assignee.AssigneeGenerator
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class TaskService(
    private val securityService: SecurityService,
    private val taskRepository: TaskRepository,
    private val costRevenueGenerator: CostsRevenueGenerator,
    private val assigneeGenerator: AssigneeGenerator,
    private val streamingEventPublisher: TaskStreamingEventPublisher,
    private val lifeCycleEventPublisher: TaskLifecycleEventPublisher,
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
        task.publicId = UUID.randomUUID()
        task.assignee = assigneeGenerator.assignee()
        task.cost = costRevenueGenerator.generateCost(task)
        task.revenue = costRevenueGenerator.generateRevenue(task)

        transaction {
            afterCommit {
                streamingEventPublisher.created(task)
                lifeCycleEventPublisher.created(task)
            }
        }

        return taskRepository.save(task)
    }

    @Transactional
    @Throws(EntityNotFoundException::class)
    fun close(id: UUID): Task {
        val task = taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException() }

        require(task.assignee?.publicId == securityService.currentUserId())

        task.status = TaskStatus.CLOSED

        transaction {
            afterCommit {
                lifeCycleEventPublisher.closed(task)
            }
        }
        return taskRepository.save(task)
    }

    @Transactional
    fun reassign(): List<Task> {
        val tasks = taskRepository.streamByStatus(TaskStatus.OPEN)
            .use { stream ->
                stream.peek { it.assignee = assigneeGenerator.assignee() }
                    .map { taskRepository.save(it) }
                    .toList()
            }

        transaction {
            afterCommit {
                tasks.forEach {
                    lifeCycleEventPublisher.reassigned(it)
                }
            }
        }

        return tasks
    }
}