package io.averkhoglyad.popug.tasks.endpoint

import io.averkhoglyad.popug.tasks.entity.Task
import io.averkhoglyad.popug.tasks.service.UserTaskService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/tasks")
@PreAuthorize("isAuthenticated()")
class UserTaskEndpoint(private val service: UserTaskService) {

    @GetMapping(params = ["!own"])
    fun listAll(pageable: Pageable): Page<Task> {
        require(pageable.isPaged)
        return service.findList(pageable)
    }

    @GetMapping(params = ["own"])
    fun listOwn(pageable: Pageable): Page<Task> {
        require(pageable.isPaged)
        return service.findOwnList(pageable)
    }

    @GetMapping("/{id}")
    fun details(@PathVariable id: UUID): Task {
        return service.findEntity(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody task: Task): Task {
        return service.create(task)
    }

    @PutMapping(path = ["/{id}"], params = ["close"])
    fun close(@PathVariable id: UUID): Task {
        return service.close(id)
    }
}