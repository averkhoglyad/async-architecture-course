package io.averkhoglyad.popug.accounting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AccountingApp

fun main(args: Array<String>) {
    runApplication<AccountingApp>(*args)
}

