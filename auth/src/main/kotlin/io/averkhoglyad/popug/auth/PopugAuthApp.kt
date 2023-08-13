package io.averkhoglyad.popug.auth

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
class PopugAuthService

fun main(args: Array<String>) {
    runApplication<PopugAuthService>(*args)
}
