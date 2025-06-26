package org.example.Solver.incomplete

import org.example.Heuristics.Heuristic
import org.example.Order
import org.example.Slab
import org.example.Solver.BaseSolver
import org.example.State
import org.example.VariableSelection.VariableSelectionHeuristic

class BeamSearchSolver(
    orders: List<Order>,
    slabCapacities: List<Int>,
    private val heuristic: Heuristic,
    private val variableSelectionHeuristic: VariableSelectionHeuristic,
    private val beamWidth: Int = 5
): BaseSolver(orders, slabCapacities) {

    override fun solve(): State? {
        var currentStates = listOf(
            State(
                orderAssignments = emptyMap(),
                slabs = emptyMap(),
                gCost = 0,
                hCost = heuristic.estimate(emptyMap(), orders, slabCapacities)
            )
        )
        var bestSolution: State? = null
        while(currentStates.isNotEmpty()){
            if (currentStates.any { it.orderAssignments.size == orders.size}) {
                bestSolution = currentStates.filter { it.orderAssignments.size == orders.size }
                    .minByOrNull { it.gCost }
                break
            }
            val allSuccessors = currentStates.flatMap { generateSuccessors(it) }
            currentStates = allSuccessors.sortedBy { it.fCost() }.take(beamWidth)
        }
        return bestSolution
    }

    override fun generateSuccessors(state: State): List<State> {
        val successors = mutableListOf<State>()

        val unassignedOrders = orders.filter { it.id !in state.orderAssignments.keys }
        if (unassignedOrders.isEmpty()) return successors

        val nextOrder = variableSelectionHeuristic.selectNextOrder(unassignedOrders, state)

        // In den ersten passenden Slab einfügen
        for ((slabId, slab) in state.slabs){
            if (canAddOrderToSlab(nextOrder, slab)){
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
        // In neuen Slab einfügen
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
            }
        }
        return successors
    }
}











