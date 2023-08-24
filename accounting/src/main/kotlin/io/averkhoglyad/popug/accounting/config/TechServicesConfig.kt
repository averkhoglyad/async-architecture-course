package io.averkhoglyad.popug.accounting.config

import io.averkhoglyad.popug.accounting.service.notification.NoopNotificationSenderImpl
import io.averkhoglyad.popug.accounting.service.notification.NotificationSender
import io.averkhoglyad.popug.accounting.service.payment.NoopPaymentGatewayImpl
import io.averkhoglyad.popug.accounting.service.payment.PaymentGateway
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TechServicesConfig {

    @Bean
    @ConditionalOnMissingBean(NotificationSender::class)
    fun noopNotificationGateway(): NotificationSender = NoopNotificationSenderImpl()

    @Bean
    @ConditionalOnMissingBean(PaymentGateway::class)
    fun noopPaymentGateway(): PaymentGateway = NoopPaymentGatewayImpl()

}