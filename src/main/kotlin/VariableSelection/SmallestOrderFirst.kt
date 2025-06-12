package org.example.VariableSelection

import org.example.Order
import org.example.State

object SmallestOrderFirst : VariableSelectionHeuristic {
    override fun selectNextOrder(unassignedOrders: List<Order>, state: State): Order {
        return unassignedOrders.minByOrNull { it.size }!!
    }
}