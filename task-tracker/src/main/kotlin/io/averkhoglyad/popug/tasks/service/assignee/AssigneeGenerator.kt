package io.averkhoglyad.popug.tasks.service.assignee

import io.averkhoglyad.popug.tasks.persistence.entity.UserEntity

interface AssigneeGenerator {

    fun assignee(): UserEntity

}