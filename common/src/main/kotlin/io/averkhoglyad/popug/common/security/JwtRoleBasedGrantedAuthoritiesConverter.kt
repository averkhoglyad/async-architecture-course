package io.averkhoglyad.popug.common.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class JwtRoleBasedGrantedAuthoritiesConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        return jwt.getClaim<List<String>>("roles")
            .map { SimpleGrantedAuthority("ROLE_$it") }
    }
}
