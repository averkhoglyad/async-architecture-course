package io.averkhoglyad.popug.auth.config

import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import io.averkhoglyad.popug.auth.util.generateRsa
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.SecurityConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher
import java.util.stream.Collectors


@Configuration(proxyBeanMethods = false)
class SecurityConfig {

    @Bean
    @Order(1)
    fun authServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http.getConfigurer<OAuth2AuthorizationServerConfigurer>()
            .oidc(withDefaults())
        http
            .csrf { it.disable() }
            .exceptionHandling {
                it.defaultAuthenticationEntryPointFor(
                    LoginUrlAuthenticationEntryPoint("/login"),
                    MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            }
            .oauth2ResourceServer {
                it.jwt(withDefaults())
            }
        return http.build()
    }

    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/actuator/**").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin(withDefaults())
        return http.build()
    }

    @Bean
    fun jwkSource(): JWKSource<com.nimbusds.jose.proc.SecurityContext> {
        val rsaKey = generateRsa()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector: JWKSelector, _ ->
            jwkSelector.select(jwkSet)
        }
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<com.nimbusds.jose.proc.SecurityContext>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun users(): UserDetailsService {
        val user = User.builder()
            .username("admin")
            .password("{noop}password")
            .roles("user")
            .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun jwtCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer<JwtEncodingContext> { context: JwtEncodingContext ->
            if (context.tokenType == OAuth2TokenType.ACCESS_TOKEN) {
                val principal: Authentication = context.getPrincipal()
                val authorities: Set<String> = principal.authorities
                    .stream()
                    .map { it.authority.removePrefix("ROLE_") }
                    .collect(Collectors.toSet())
                context.claims.claim("roles", authorities)
            }
        }
    }
}

inline fun <reified C : SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity>>
        HttpSecurity.getConfigurer(): C = this.getConfigurer(C::class.java)
