package org.example.VariableSelection

import org.example.Order
import org.example.State

object TightestFitOrderFirst : VariableSelectionHeuristic {
    override fun selectNextOrder(unassignedOrders: List<Order>, state: State): Order {
        val unassigned = unassignedOrders.filter { it.id !in state.orderAssignments.keys }
        return unassigned.minByOrNull { order ->
            state.slabs.values
                .filter { it.capacity - it.assignedOrders.sumOf { o -> o.size } >= order.size }
                .minOfOrNull { it.capacity - it.assignedOrders.sumOf { o -> o.size } - order.size } ?: Int.MAX_VALUE
        } ?: unassigned.first()
    }
}