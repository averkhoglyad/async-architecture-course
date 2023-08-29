package io.averkhoglyad.popug.accounting.core.service

import io.averkhoglyad.popug.accounting.core.event.streaming.TaskDto
import io.averkhoglyad.popug.accounting.core.persistence.entity.Task
import io.averkhoglyad.popug.accounting.core.persistence.repository.TaskRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class TaskService(
    private val repository: TaskRepository
) {

    @Transactional
    fun create(dto: TaskDto) {
        val task = dto.toEntity()
        repository.save(task)
    }

    @Transactional
    fun update(dto: TaskDto) {
        repository
            .findByPublicId(dto.publicId)
            .ifPresent { repository.save(dto.toEntity(it)) }
    }

    @Transactional
    fun delete(userPublicId: UUID) {
        repository
            .findByPublicId(userPublicId)
            .ifPresent { repository.save(it.apply { isActive = false }) }
    }
}

fun TaskDto.toEntity(): Task = toEntity(Task())

fun TaskDto.toEntity(entity: Task): Task = entity
    .also {
        it.publicId = this.publicId
        it.title = this.title
        it.description = this.description
    }
