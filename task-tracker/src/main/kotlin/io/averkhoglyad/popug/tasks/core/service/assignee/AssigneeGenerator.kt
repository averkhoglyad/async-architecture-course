package io.averkhoglyad.popug.tasks.core.service.assignee

import io.averkhoglyad.popug.tasks.core.persistence.entity.UserEntity

interface AssigneeGenerator {

    fun assignee(): UserEntity

}