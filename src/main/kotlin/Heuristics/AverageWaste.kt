package org.example.Heuristics

import org.example.Order

object AverageWaste: Heuristic {
    override fun estimate(orderAssignments: Map<Int,Int>, unassignedOrders: List<Order>, slabCapacities: List<Int>): Int {
        val remainingOrders = unassignedOrders.filter { it.id !in orderAssignments.keys }
        val remainingSize = remainingOrders.sumOf { it.size }
        val avgCapacity = slabCapacities.average()
        return (remainingSize / avgCapacity).toInt() * 5 // Beispielwert, für minimalen Verschnitt
    }
}