package io.averkhoglyad.popug.tasks.service.accounting

import io.averkhoglyad.popug.tasks.entity.Task
import org.springframework.stereotype.Component
import java.util.concurrent.ThreadLocalRandom

@Component
class RandomBasedCostsRevenueGeneratorImpl : CostsRevenueGenerator {

    override fun generateCost(task: Task): Int {
        return ThreadLocalRandom.current().nextInt(-20, -10)
    }

    override fun generateRevenue(task: Task): Int {
        return ThreadLocalRandom.current().nextInt(20, 40)
    }
}