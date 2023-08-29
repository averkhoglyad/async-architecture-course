package io.averkhoglyad.popug.accounting.core.service.notification

import io.averkhoglyad.popug.accounting.core.persistence.entity.UserEntity
import io.averkhoglyad.popug.common.log4j
import org.springframework.stereotype.Component

@Component
class NoopNotificationSenderImpl : NotificationSender {

    private val log by log4j()

    override fun send(user: UserEntity, message: String) {
        log.info("Sending message to User#{} {}:\n{}", user.publicId, user.publicId, message)
    }
}