package io.averkhoglyad.popug.tasks.core.service.accounting

import io.averkhoglyad.popug.tasks.core.persistence.entity.Task

interface CostsRevenueGenerator {

    fun generateCost(task: Task): Int
    fun generateRevenue(task: Task): Int

}