package org.example.VariableSelection

import org.example.Order
import org.example.State

interface VariableSelectionHeuristic {
    fun selectNextOrder(unassignedOrders: List<Order>, state: State): Order
}