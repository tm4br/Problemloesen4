package org.example.VariableSelection

import org.example.Order
import org.example.State

object LargestOrderFirst : VariableSelectionHeuristic {
    override fun selectNextOrder(unassignedOrders: List<Order>, state: State): Order {
        return unassignedOrders.maxByOrNull { it.size }!!
    }
}