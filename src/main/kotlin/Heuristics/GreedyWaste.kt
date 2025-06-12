package org.example.Heuristics

import org.example.Order

object GreedyWaste: Heuristic {

    override fun estimate(orderAssignments: Map<Int, Int>, unassignedOrders: List<Order>, slabCapacities: List<Int>): Int {
        val remainingOrders = unassignedOrders.filter { it.id !in orderAssignments.keys }
        if (remainingOrders.isEmpty()) return 0

        val sortedOrders = remainingOrders.sortedByDescending { it.size }
        val sortedCapacities = slabCapacities.sortedDescending()

        var waste = 0
        var i = 0

        while (i < sortedOrders.size) {
            var slabUsed = 0
            val slabCapacity = sortedCapacities.firstOrNull() ?: return waste

            //so viele Aufträge wie möglich in aktuellen Slab
            while (i < sortedOrders.size && slabUsed + sortedOrders[i].size <= slabCapacity) {
                slabUsed += sortedOrders[i].size
                i++
            }
            waste += slabCapacity - slabUsed
        }
        return waste
    }
}