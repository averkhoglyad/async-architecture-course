package io.averkhoglyad.popug.auth.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings


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
