package io.averkhoglyad.popug.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.security.oauth2.authorizationserver")
data class AuthServerProperties(
    val issuer: String
)
