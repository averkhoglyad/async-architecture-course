package io.averkhoglyad.popug.accounting.core.persistence.repository

import io.averkhoglyad.popug.accounting.core.persistence.entity.UserAccount
import io.averkhoglyad.popug.accounting.core.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserAccountRepository : JpaRepository<UserAccount, UUID> {

    fun findByOwner(owner: UserEntity): Optional<UserAccount>

    @Modifying
    @Query("UPDATE UserAccount a SET a.balance=a.balance+?2 WHERE a=?1")
    fun fundTransfer(account: UserAccount, amount: Int)

    @Query("SELECT a FROM UserAccount a INNER JOIN a.owner u WHERE a.balance>0 AND u.role=\"USER\"")
    fun findTop100ByBalanceIsPositiveAndUserRoleIsUser(): List<UserAccount>

}
