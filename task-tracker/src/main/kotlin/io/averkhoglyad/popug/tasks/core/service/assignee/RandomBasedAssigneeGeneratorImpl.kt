package io.averkhoglyad.popug.tasks.core.service.assignee

import io.averkhoglyad.popug.tasks.core.persistence.entity.UserEntity
import io.averkhoglyad.popug.tasks.core.persistence.entity.UserRole
import io.averkhoglyad.popug.tasks.core.persistence.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class RandomBasedAssigneeGeneratorImpl(
    private val repo: UserRepository
) : AssigneeGenerator {

    override fun assignee(): UserEntity {
        val total = repo.countByRole(UserRole.USER)
        val offset = Random.nextInt(0, total.toInt())
        val position = Pageable.ofSize(1).withPage(offset)
        val list = repo.findByRole(UserRole.USER, position)
        require(list.isNotEmpty())
        return list.first()
    }
}