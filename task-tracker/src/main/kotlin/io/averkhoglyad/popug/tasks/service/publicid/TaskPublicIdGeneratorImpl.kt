package io.averkhoglyad.popug.tasks.service.publicid

import io.averkhoglyad.popug.auth.service.publicid.PublicIdGenerator
import io.averkhoglyad.popug.tasks.persistence.entity.Task
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class TaskPublicIdGeneratorImpl : PublicIdGenerator<Task> {
    override fun generate(entity: Task): String {
        val title = entity.title
            .chars()
            .map { if (Character.isAlphabetic(it)) Character.toLowerCase(it) else '-'.code }
            .collect(::StringBuilder, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString()
        return "${title}-${Instant.now().toEpochMilli()}"
    }
}
