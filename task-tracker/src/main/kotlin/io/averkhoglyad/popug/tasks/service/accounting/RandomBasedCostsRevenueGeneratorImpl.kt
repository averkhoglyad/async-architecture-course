package io.averkhoglyad.popug.tasks.service.accounting

import io.averkhoglyad.popug.tasks.entity.Task
import org.springframework.stereotype.Component
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

@Component
class RandomBasedCostsRevenueGeneratorImpl : CostsRevenueGenerator {

    override fun generateCost(task: Task): Int {
        return Random.nextInt(10, 20)
    }

    override fun generateRevenue(task: Task): Int {
        return Random.nextInt(20, 40)
    }
}
