package io.averkhoglyad.popug.tasks.service.assignee

import io.averkhoglyad.popug.tasks.entity.User
import io.averkhoglyad.popug.tasks.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Component

@Component
class RandomBasedAssigneeGeneratorImpl(private val repo: UserRepository) : AssigneeGenerator {
    override fun assignee(): User {
        return repo.randomUser()
            .orElseThrow { EntityNotFoundException() }
    }
}