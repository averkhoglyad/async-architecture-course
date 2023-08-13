package io.averkhoglyad.popug.tasks.repository

import io.averkhoglyad.popug.tasks.entity.Task
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TaskRepository : JpaRepository<Task, UUID>