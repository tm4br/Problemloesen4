package org.example.Solver

import org.example.Order
import org.example.Slab
import org.example.State

abstract class BaseSolver (
    protected val orders: List<Order>,
    protected val slabCapacities: List<Int>
): Solver{
    /**
     * Überprüft, ob ein Auftrag zu einem Slab hinzugefügt werden kann:
     * - Farbe muss einzigartig sein
     * - Kapazität darf nicht überschritten werden
     */
    open fun canAddOrderToSlab(order: Order, slab: Slab): Boolean {
        val colors = slab.assignedOrders.map { it.color }.toSet()
        val totalSize = slab.assignedOrders.sumOf { it.size }
        return (order.color !in colors) && (totalSize + order.size <= slab.capacity)
    }

    /**
     * Berechnet den Gesamtverschnitt aller aktuell belegten Slabs
     */
    open fun calculateWaste(slabs: Map<Int, Slab>): Int {
        return slabs.values.sumOf { slab ->
            val used = slab.assignedOrders.sumOf { it.size }
            val waste = slab.capacity - used
            maxOf(0,waste)
        }
    }

    abstract fun generateSuccessors(state: State): List<State>
}