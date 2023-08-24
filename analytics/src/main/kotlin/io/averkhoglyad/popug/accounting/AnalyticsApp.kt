package io.averkhoglyad.popug.accounting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AnalyticsApp

fun main(args: Array<String>) {
    runApplication<AnalyticsApp>(*args)
}

