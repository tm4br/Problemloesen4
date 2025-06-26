package org.example.Solver.complete

import org.example.Heuristics.Heuristic
import org.example.Order
import org.example.Slab
import org.example.Solver.BaseSolver
import org.example.State
import org.example.VariableSelection.VariableSelectionHeuristic
import java.util.PriorityQueue

class CompleteSolver(
    orders: List<Order>,
    slabCapacities: List<Int>,
    private val heuristic: Heuristic,
    private val variableSelection: VariableSelectionHeuristic
): BaseSolver(orders, slabCapacities) {

    override fun solve(): State? {
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

    override fun generateSuccessors(state: State): List<State> {
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
}












