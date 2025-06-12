package org.example.Heuristics

import org.example.Order

object BestFit : Heuristic {
    override fun estimate(orderAssignments: Map<Int, Int>, unassignedOrders: List<Order>, slabCapacities: List<Int>): Int {
        val remainingOrders = unassignedOrders.filter { it.id !in orderAssignments.keys }
        var estimatedWaste = 0
        for (order in remainingOrders) {
            val bestFitWaste = slabCapacities
                .filter { it >= order.size }
                .minOfOrNull { it - order.size } ?: 0
            estimatedWaste += bestFitWaste
        }
        return estimatedWaste
    }
}