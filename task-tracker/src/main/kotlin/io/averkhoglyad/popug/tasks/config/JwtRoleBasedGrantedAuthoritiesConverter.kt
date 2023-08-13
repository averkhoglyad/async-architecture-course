package io.averkhoglyad.popug.tasks.config

import org.springframework.core.convert.converter.Converter
import org.springframework.lang.NonNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class JwtRoleBasedGrantedAuthoritiesConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    @NonNull
    override fun convert(@NonNull jwt: Jwt): Collection<GrantedAuthority> {
        return jwt.getClaim<List<String>>("roles")
            .map { SimpleGrantedAuthority("ROLE_$it") }
    }
}