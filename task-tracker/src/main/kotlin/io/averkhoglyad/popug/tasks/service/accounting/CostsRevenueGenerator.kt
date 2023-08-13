package io.averkhoglyad.popug.tasks.service.accounting

import io.averkhoglyad.popug.tasks.entity.Task

interface CostsRevenueGenerator {

    fun generateCost(task: Task): Int
    fun generateRevenue(task: Task): Int

}