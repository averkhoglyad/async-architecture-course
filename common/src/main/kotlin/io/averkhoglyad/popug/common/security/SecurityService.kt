package io.averkhoglyad.popug.common.security

import org.springframework.security.oauth2.jwt.Jwt
import java.util.UUID

interface SecurityService {
    
    fun currentJwtToken(): Jwt?

    fun currentUserId(): UUID? = UUID.fromString(currentJwtToken()?.subject)

}
