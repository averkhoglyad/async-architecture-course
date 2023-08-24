package io.averkhoglyad.popug.accounting.persistence.repository

import io.averkhoglyad.popug.accounting.persistence.entity.Task
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TaskRepository : JpaRepository<Task, UUID> {

    fun findByPublicId(publicId: UUID): Optional<Task>

}
