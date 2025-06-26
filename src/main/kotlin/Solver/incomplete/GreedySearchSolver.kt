package org.example.Solver.incomplete

import org.example.Heuristics.Heuristic
import org.example.Order
import org.example.Slab
import org.example.Solver.BaseSolver
import org.example.State
import org.example.VariableSelection.VariableSelectionHeuristic
import java.util.ArrayDeque

class GreedySearchSolver(
    orders: List<Order>,
    slabCapacities: List<Int>,
    private val heuristic: Heuristic,
    private val variableSelection: VariableSelectionHeuristic
): BaseSolver(orders, slabCapacities) {

    /**
     * Tiefe Suche mit Greedy-Strategie
     */
    override fun solve(): State? {
        var current = State(
            orderAssignments = emptyMap(),
            slabs = emptyMap(),
            gCost = 0,
            hCost = heuristic.estimate(emptyMap(), orders, slabCapacities)
        )

        while (current.orderAssignments.size < orders.size) {
            val successors = generateSuccessors(current)
            if (successors.isEmpty()) break

            // Greedy - nur bester Nachfolger
            current = successors.minByOrNull { it.hCost } ?: break
        }
        return if (current.orderAssignments.size == orders.size)  current else null
    }

    override fun generateSuccessors(state: State): List<State> {
        val successors = mutableListOf<State>()

        val unassignedOrders = orders.filter { it.id !in state.orderAssignments.keys }
        if (unassignedOrders.isEmpty()) return successors

        val nextOrder = variableSelection.selectNextOrder(unassignedOrders, state)

        // In den ersten passenden Slab einfügen
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
                // Sobald ein passender Slab gefunden wurde, abbrechen
                break
            }
        }
        // Neuen Slab anlegen, falls nötig
        if (successors.isEmpty()){
            for (capacity in slabCapacities){
                if (nextOrder.size <= capacity){
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
                    // Sobald ein passender Slab gefunden wurde, abbrechen
                    break
                }
            }
        }
        return successors
    }
}

















