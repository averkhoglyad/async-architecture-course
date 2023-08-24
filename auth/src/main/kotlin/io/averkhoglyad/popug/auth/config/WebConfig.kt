package io.averkhoglyad.popug.auth.config

import io.averkhoglyad.popug.common.mvc.ExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebConfig {

    @Bean
    fun exceptionHandler() = ExceptionHandler()

}