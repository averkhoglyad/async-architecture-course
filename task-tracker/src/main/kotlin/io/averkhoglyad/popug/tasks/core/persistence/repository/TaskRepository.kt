package io.averkhoglyad.popug.tasks.core.persistence.repository

import io.averkhoglyad.popug.tasks.core.persistence.entity.Task
import io.averkhoglyad.popug.tasks.core.persistence.entity.TaskStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
import java.util.stream.Stream

interface TaskRepository : JpaRepository<Task, UUID> {

    fun findByAssigneeId(assigneeId: UUID, pageable: Pageable): Page<Task>

    fun countByStatus(status: TaskStatus): Long
    fun findByStatus(status: TaskStatus): List<Task>
    fun streamByStatus(status: TaskStatus): Stream<Task>

}