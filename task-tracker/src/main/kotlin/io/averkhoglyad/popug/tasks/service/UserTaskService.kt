package io.averkhoglyad.popug.tasks.service

import io.averkhoglyad.popug.tasks.entity.Task
import io.averkhoglyad.popug.tasks.entity.TaskStatus
import io.averkhoglyad.popug.tasks.repository.TaskRepository
import io.averkhoglyad.popug.tasks.service.accounting.CostsRevenueGenerator
import io.averkhoglyad.popug.tasks.service.assignee.AssigneeGenerator
import io.averkhoglyad.popug.tasks.util.transaction
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalStateException
import java.util.UUID

@Service
class UserTaskService(private val repo: TaskRepository,
                      private val costRevenueGenerator: CostsRevenueGenerator,
                      private val assigneeGenerator: AssigneeGenerator) {

    @Transactional(readOnly = true)
    fun findList(pageable: Pageable): Page<Task> {
        return repo.findAll(pageable)
    }

    @Transactional(readOnly = true)
    @Throws(EntityNotFoundException::class)
    fun findEntity(id: UUID): Task {
        return repo.findById(id)
            .orElseThrow { EntityNotFoundException() }
    }

    @Transactional
    fun create(task: Task): Task {
        val assignee = try {
            assigneeGenerator.assignee()
        } catch (e: EntityNotFoundException) {
            throw IllegalStateException("No users to be assigned")
        }
        task.assignee = assignee
        task.userCost = costRevenueGenerator.generateCost(task)
        task.userRevenue = costRevenueGenerator.generateRevenue(task)

        transaction {
            afterCommit {
                // TODO: Send event
            }
        }
        return repo.save(task)
    }

    @Transactional
    @Throws(EntityNotFoundException::class)
    fun close(id: UUID): Task {
        // TODO: Check user is assignee
        val task = repo.findById(id)
            .orElseThrow { EntityNotFoundException() }
        task.status = TaskStatus.CLOSED
        transaction {
            afterCommit {
                // TODO: Send event
            }
        }
        return repo.save(task)
    }
}