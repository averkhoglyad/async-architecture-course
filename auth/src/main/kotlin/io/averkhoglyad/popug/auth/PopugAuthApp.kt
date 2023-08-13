package io.averkhoglyad.popug.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PopugAuthService

fun main(args: Array<String>) {
    runApplication<PopugAuthService>(*args)
}
