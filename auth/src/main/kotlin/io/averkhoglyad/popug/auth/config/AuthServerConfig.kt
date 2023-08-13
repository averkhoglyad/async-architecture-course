package io.averkhoglyad.popug.auth.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration


@Configuration
@EnableConfigurationProperties(value = [AuthServerProperties::class])
class AuthServerConfig {

    @Bean
    fun authorizationServerSettings(authorizationServerProperties: AuthServerProperties): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder()
            .issuer(authorizationServerProperties.issuer)
            .build()
    }
}
