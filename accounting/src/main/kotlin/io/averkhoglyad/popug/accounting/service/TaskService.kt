package io.averkhoglyad.popug.accounting.service

import io.averkhoglyad.popug.accounting.event.streaming.task.TaskDto
import io.averkhoglyad.popug.accounting.persistence.entity.Task
import io.averkhoglyad.popug.accounting.persistence.repository.TaskRepository
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
        if (this.jiraId == null) {
            val segments = title.split(" - ".toRegex(), 2)
            if (segments.size == 1) {
                it.jiraId = ""
                it.title = this.title
            } else {
                it.jiraId = segments[0]
                it.title = segments[1]
            }
        } else {
            it.jiraId = this.jiraId
            it.title = this.title
        }
        it.description = this.description
    }
