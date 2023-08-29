package io.averkhoglyad.popug.accounting.core.service.notification

import io.averkhoglyad.popug.accounting.core.persistence.entity.UserEntity

interface NotificationSender {

    fun send(user: UserEntity, message: String)

}