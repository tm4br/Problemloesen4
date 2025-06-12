package org.example.VariableSelection

import org.example.Order
import org.example.State

object MostFrequentColorLargestFirst : VariableSelectionHeuristic {
    override fun selectNextOrder(unassignedOrders: List<Order>, state: State): Order {
        val colorFrequencies = unassignedOrders.groupingBy { it.color }.eachCount()
        val mostFrequentColor = colorFrequencies.maxByOrNull { it.value }!!.key
        return unassignedOrders
            .filter { it.color == mostFrequentColor }
            .maxByOrNull { it.size }!!
    }
}