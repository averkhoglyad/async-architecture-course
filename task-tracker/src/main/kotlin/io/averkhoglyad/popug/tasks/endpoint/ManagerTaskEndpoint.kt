package io.averkhoglyad.popug.tasks.endpoint

import io.averkhoglyad.popug.tasks.core.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tasks")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
class ManagerTaskEndpoint(private val service: TaskService) {

    @PostMapping(params = ["reassign"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun reassign() {
        service.reassign()
    }
}