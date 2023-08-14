package io.averkhoglyad.popug.tasks.config

import io.averkhoglyad.popug.tasks.endpoint.UserModificationHandler
import io.averkhoglyad.popug.tasks.event.UserDto
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import java.util.function.Consumer

@Configuration
class StreamingConfig {

    @Bean
    fun userModified(handler: UserModificationHandler) = Consumer<Message<UserDto>>(handler::handleUserModification)

}