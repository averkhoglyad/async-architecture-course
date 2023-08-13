package io.averkhoglyad.popug.tasks.endpoint

import io.averkhoglyad.popug.tasks.entity.Task
import io.averkhoglyad.popug.tasks.service.UserTaskService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/tasks")
@PreAuthorize("isAuthenticated()")
class UserTaskEndpoint(private val service: UserTaskService) {

    @GetMapping
    fun list(pageable: Pageable): Page<Task> {
        require(pageable.isPaged)
        return service.findList(pageable)
    }

    @GetMapping("/{id}")
    fun details(@PathVariable id: UUID): Task {
        return service.findEntity(id)
    }

    @PostMapping
    fun create(@RequestBody task: Task): Task {
        return service.create(task)
    }

    @PutMapping(path = ["/{id}"], params = ["close"])
    fun close(@PathVariable id: UUID): Task {
        return service.close(id)
    }
}