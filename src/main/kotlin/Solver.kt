package org.example

import org.example.Heuristics.Heuristic
import org.example.VariableSelection.VariableSelectionHeuristic
import java.util.PriorityQueue

class Solver(
    private val orders: List<Order>,
    private val slabCapacities: List<Int>,
    private val heuristic: Heuristic,
    private val variableSelection: VariableSelectionHeuristic
) {

    fun solve(): State? {
        /** Initialzustand: keine Order zugewiesen, keine Slabs */
        val initialState = State(
            orderAssignments = emptyMap(),
            slabs = emptyMap(),
            gCost = 0,
            hCost = heuristic.estimate(emptyMap(), orders, slabCapacities)
        )

        val openList = PriorityQueue<State>(compareBy { it.fCost() })
        val closedSet = mutableSetOf<Map<Int, Int>>()

        openList.add(initialState)

        while (openList.isNotEmpty()) {
            val current = openList.poll()

            /** Zielzustand prüfen: alle Orders zugewiesen*/
            if (current.orderAssignments.size == orders.size) {
                return current
            }
            if (current.orderAssignments in closedSet) continue
            closedSet.add(current.orderAssignments)

            /** Nachfolgezustände generieren*/
            val successors = generateSuccessors(current)

            for (next in successors) {
                openList.add(next)
            }
        }
        return null /* Kein Zielzustand gefunden*/
    }

    fun generateSuccessors(state: State): List<State> {
        val successors = mutableListOf<State>()

        /** Nächster noch nicht zugewiesener Auftrag*/
        val unassignedOrders = orders.filter { it.id !in state.orderAssignments.keys }
        if (unassignedOrders.isEmpty()) return successors

        val nextOrder = variableSelection.selectNextOrder(unassignedOrders, state)

        /** Versuch, den Auftrag in bestehende Slabs einzufügen*/
        for ((slabId, slab) in state.slabs) {
            if (canAddOrderToSlab(nextOrder, slab)) {
                val newSlab = slab.copy(assignedOrders = slab.assignedOrders + nextOrder)
                val newSlabs = state.slabs.toMutableMap().apply { put(slabId, newSlab) }
                val newG = calculateWaste(newSlabs)

                successors.add(
                    State(
                        orderAssignments = state.orderAssignments + (nextOrder.id to slabId),
                        slabs = newSlabs,
                        gCost = newG,
                        hCost = heuristic.estimate(
                            state.orderAssignments + (nextOrder.id to slabId),
                            orders,
                            slabCapacities
                        )
                    )
                )
            }
        }

        /**Versuch neuen Slab anzulegen (mit allen möglichen Kapazitäten)*/
        for (capacity in slabCapacities) {
            if (nextOrder.size <= capacity) {
                val slabId = (state.slabs.keys.maxOrNull() ?: -1) + 1
                val newSlab = Slab(capacity, listOf(nextOrder))
                val newSlabs = state.slabs.toMutableMap().apply { put(slabId, newSlab) }
                val newG = calculateWaste(newSlabs)

                successors.add(
                    State(
                        orderAssignments = state.orderAssignments + (nextOrder.id to slabId),
                        slabs = newSlabs,
                        gCost = newG,
                        hCost = heuristic.estimate(
                            state.orderAssignments + (nextOrder.id to slabId),
                            orders,
                            slabCapacities
                        )
                    )
                )
            }
        }
        return successors
    }

    /**
     * Überprüft, ob ein Auftrag zu einem Slab hinzugefügt werden kann:
     * - Farbe muss einzigartig sein
     * - Kapazität darf nicht überschritten werden
     */
    fun canAddOrderToSlab(order: Order, slab: Slab): Boolean {
        val colors = slab.assignedOrders.map { it.color }.toSet()
        val totalSize = slab.assignedOrders.sumOf { it.size }
        return (order.color !in colors) && (totalSize + order.size <= slab.capacity)
    }

    /**
     * Berechnet den Gesamtverschnitt aller aktuell belegten Slabs
     */
    fun calculateWaste(slabs: Map<Int, Slab>): Int {
        return slabs.values.sumOf { slab ->
            val used = slab.assignedOrders.sumOf { it.size }
            val waste = slab.capacity - used
            maxOf(0,waste)
        }
    }
}












