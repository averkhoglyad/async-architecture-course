package io.averkhoglyad.popug.auth.service.publicid

import io.averkhoglyad.popug.auth.entity.UserEntity
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class UserPublicIdGeneratorImpl : PublicIdGenerator<UserEntity> {
    override fun generate(entity: UserEntity): String {
        val login = entity.login
            .chars()
            .map { if (Character.isAlphabetic(it)) Character.toLowerCase(it) else '-'.code }
            .collect(::StringBuilder, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString()
        return "${login}-${Instant.now().toEpochMilli()}"
    }
}