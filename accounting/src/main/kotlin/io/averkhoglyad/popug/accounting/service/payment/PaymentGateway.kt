package io.averkhoglyad.popug.accounting.service.payment

import io.averkhoglyad.popug.accounting.persistence.entity.UserAccount

interface PaymentGateway {

    fun payOutFromAccount(account: UserAccount, amount: Int)

}