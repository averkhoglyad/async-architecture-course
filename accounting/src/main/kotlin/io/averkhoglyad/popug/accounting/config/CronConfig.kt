package io.averkhoglyad.popug.accounting.config

import io.averkhoglyad.popug.accounting.endpoint.EndOfDayHandler
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
class CronConfig(
    private val endDayHandler: EndOfDayHandler
) {

    @Scheduled(cron = "0 0 22 * * *")
    fun everyEndOfDay() {
        endDayHandler.handle()
    }

}