package io.averkhoglyad.popug.tasks.config

import io.averkhoglyad.popug.common.security.JwtRoleBasedGrantedAuthoritiesConverter
import io.averkhoglyad.popug.common.security.SecurityContextBasedSecurityService
import io.averkhoglyad.popug.common.security.SecurityService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig {

    @Bean
    fun securityService(): SecurityService = SecurityContextBasedSecurityService()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .oauth2ResourceServer {
                it.jwt { configurer -> configurer.jwtAuthenticationConverter(jwtAuthenticationConverter()) }
            }
        return http.build()
    }

    private fun jwtAuthenticationConverter(): Converter<Jwt, AbstractAuthenticationToken> {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter())
        return jwtAuthenticationConverter
    }

    private fun jwtGrantedAuthoritiesConverter(): Converter<Jwt, Collection<GrantedAuthority>> {
        return JwtRoleBasedGrantedAuthoritiesConverter()
    }
}
