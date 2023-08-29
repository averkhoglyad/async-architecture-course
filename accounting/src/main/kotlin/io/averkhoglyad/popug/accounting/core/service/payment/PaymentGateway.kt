package io.averkhoglyad.popug.accounting.core.service.payment

import io.averkhoglyad.popug.accounting.core.persistence.entity.UserAccount

interface PaymentGateway {

    fun payOutFromAccount(account: UserAccount, amount: Int)

}