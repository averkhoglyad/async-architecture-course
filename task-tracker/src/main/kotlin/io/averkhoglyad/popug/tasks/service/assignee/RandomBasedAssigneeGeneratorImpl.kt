package io.averkhoglyad.popug.tasks.service.assignee

import io.averkhoglyad.popug.tasks.entity.User
import io.averkhoglyad.popug.tasks.entity.UserRole
import io.averkhoglyad.popug.tasks.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class RandomBasedAssigneeGeneratorImpl(
    private val repo: UserRepository
) : AssigneeGenerator {

    override fun assignee(): User {
        val total = repo.countByRole(UserRole.USER)
        val offset = Random.nextInt(0, total.toInt())
        val position = Pageable.ofSize(1).withPage(offset)
        val list = repo.findFirstByRole(UserRole.USER, position)
        require(list.isNotEmpty())
        return list.first()
    }
}