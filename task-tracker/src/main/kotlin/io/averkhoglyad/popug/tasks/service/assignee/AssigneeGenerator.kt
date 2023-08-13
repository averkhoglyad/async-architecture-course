package io.averkhoglyad.popug.tasks.service.assignee

import io.averkhoglyad.popug.tasks.entity.User

interface AssigneeGenerator {

    fun assignee(): User

}