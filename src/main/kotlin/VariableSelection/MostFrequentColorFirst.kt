package org.example.VariableSelection

import org.example.Order
import org.example.State

object MostFrequentColorFirst : VariableSelectionHeuristic {
    override fun selectNextOrder(unassignedOrders: List<Order>, state: State): Order {
        val colorFrequency = unassignedOrders.groupingBy { it.color }.eachCount()
        val mostFrequentColor = colorFrequency.maxByOrNull { it.value }!!.key
        return unassignedOrders.first { order -> order.color == mostFrequentColor }
    }
}