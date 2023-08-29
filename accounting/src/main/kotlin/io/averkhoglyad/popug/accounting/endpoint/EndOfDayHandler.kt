package io.averkhoglyad.popug.accounting.endpoint

import io.averkhoglyad.popug.accounting.core.persistence.repository.UserAccountRepository
import io.averkhoglyad.popug.accounting.core.service.UserAccountService
import org.springframework.stereotype.Component

@Component
class EndOfDayHandler(
    private val accountService: UserAccountService,
    private val accountRepository: UserAccountRepository
) {

    fun handle() {
        var accounts = accountRepository.findTop100ByBalanceIsPositiveAndUserRoleIsUser()
        while (accounts.isNotEmpty()) {
            accounts.forEach { accountService.withdrawOutFromBalance(it) }
            accounts = accountRepository.findTop100ByBalanceIsPositiveAndUserRoleIsUser()
        }
    }
}