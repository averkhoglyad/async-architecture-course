package io.averkhoglyad.popug.accounting.core.service.payment

import io.averkhoglyad.popug.accounting.core.persistence.entity.UserAccount
import io.averkhoglyad.popug.common.log4j

class NoopPaymentGatewayImpl : PaymentGateway {

    private val log by log4j()

    override fun payOutFromAccount(account: UserAccount, amount: Int) {
        log.info("Pay out from Account {} ({}) amount \${}", account.id, account.owner.name, amount)
    }
}