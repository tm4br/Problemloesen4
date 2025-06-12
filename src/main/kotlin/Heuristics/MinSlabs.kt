package org.example.Heuristics

import org.example.Order

object MinSlabs: Heuristic {
    override fun estimate(orderAssignments: Map<Int, Int>, orders: List<Order>, slabCapacities: List<Int>): Int {
        val remainingOrders = orders.filter { it.id !in orderAssignments.keys}
        if (remainingOrders.isEmpty()) return 0

        val totalSize = remainingOrders.sumOf { it.size }
        val largestSlab = slabCapacities.maxOrNull() ?: return 1

        //Anzahl benötigter Slabs * durchschnittlicher Verschnitt
        val estimatedNumSlabs = Math.ceil(totalSize / largestSlab.toDouble()).toInt()
        val avgWaste = largestSlab * 0.1 // Schätzungsweise 10% Verschnitt

        return (estimatedNumSlabs * avgWaste).toInt()
    }
}