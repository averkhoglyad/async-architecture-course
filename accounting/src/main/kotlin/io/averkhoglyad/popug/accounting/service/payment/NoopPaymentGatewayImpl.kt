package io.averkhoglyad.popug.accounting.service.payment

import io.averkhoglyad.popug.accounting.persistence.entity.UserAccount
import io.averkhoglyad.popug.common.log4j

class NoopPaymentGatewayImpl : PaymentGateway {

    private val log by log4j()

    override fun payOutFromAccount(account: UserAccount, amount: Int) {
        log.info("Pay out from Account {} ({}) amount \${}", account.id, account.owner.name, amount)
    }
}