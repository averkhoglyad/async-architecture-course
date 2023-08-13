package io.averkhoglyad.popug.tasks.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class SecurityContextBasedSecurityService : SecurityService {

    override fun currentJwtToken(): Jwt? {
        return SecurityContextHolder.getContext().authentication?.principal as? Jwt
    }
}
