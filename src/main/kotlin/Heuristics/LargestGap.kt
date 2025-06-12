package org.example.Heuristics

import org.example.Order

object LargestGap: Heuristic {
    override fun estimate(orderAssignments: Map<Int, Int>, orders: List<Order>, slabCapacities: List<Int>): Int {
        val remainingOrders = orders.filter { it.id !in orderAssignments.keys }
        if (remainingOrders.isEmpty()) return 0

        val largestOrder = remainingOrders.maxOf { it.size }
        val largestSlab = slabCapacities.maxOrNull() ?: 1

        return if (largestOrder <= largestSlab) {
            (largestSlab - largestOrder) * (remainingOrders.size / 2) //geschätzter Gesamtverschnitt
        } else {
            (largestOrder - largestSlab) * (remainingOrders.size) // Strafkosten falls zu hoch
        }
    }
}