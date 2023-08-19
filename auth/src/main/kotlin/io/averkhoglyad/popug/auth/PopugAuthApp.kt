package io.averkhoglyad.popug.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.Instant

@SpringBootApplication
class PopugAuthService

fun main(args: Array<String>) {
    runApplication<PopugAuthService>(*args)
}
