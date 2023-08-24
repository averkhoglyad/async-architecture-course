package io.averkhoglyad.popug.accounting.service.notification

import io.averkhoglyad.popug.accounting.persistence.entity.UserEntity

interface NotificationSender {

    fun send(user: UserEntity, message: String)

}