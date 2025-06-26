package org.example.Solver.incomplete

import org.example.Heuristics.Heuristic
import org.example.Order
import org.example.Slab
import org.example.Solver.BaseSolver
import org.example.State
import org.example.VariableSelection.VariableSelectionHeuristic

class RandomSearchSolver(
    orders: List<Order>,
    slabCapacities: List<Int>,
    private val heuristic: Heuristic,
    private val variableSelection: VariableSelectionHeuristic,
    private val maxIterations: Int = 10
) : BaseSolver(orders, slabCapacities) {

    /**
     * Zufällie Suche
     * generiert zufällig Zustände und wählt den besten aus
     */
    override fun solve(): State? {
        val initialState = State(
            orderAssignments = emptyMap(),
            slabs = emptyMap(),
            gCost = 0,
            hCost = 0
        )

        var bestSolution: State? = null
        var bestWaste = Int.MAX_VALUE

        repeat(maxIterations){
            val solution = buildRandomSolution(initialState)
            if (solution != null && solution.gCost < bestWaste) {
                bestSolution = solution
                bestWaste = solution.gCost
            }
        }
        return bestSolution
    }

    fun buildRandomSolution(initialState: State): State?{
        var currentState = initialState
        while (currentState.orderAssignments.size < orders.size){
            val successors = generateSuccessors(currentState)
            if (successors.isEmpty()) return null

            //zufälligen Nachfolger wählen
            currentState = successors.random()
        }
        return currentState
    }

    override fun generateSuccessors(state: State): List<State> {
        val successors = mutableListOf<State>()

        val unassignedOrders = orders.filter { it.id !in state.orderAssignments.keys }
        if (unassignedOrders.isEmpty()) return successors

        val nextOrder = unassignedOrders.random()

        // In den aktuellen Slab einfügen
        for ((slabId, slab) in state.slabs){
            if (canAddOrderToSlab(nextOrder, slab)){
                val newSlab = slab.copy(assignedOrders = slab.assignedOrders + nextOrder)
                val newSlabs = state.slabs.toMutableMap().apply { put(slabId, newSlab) }
                val newG = calculateWaste(newSlabs)

                successors.add(
                    State(
                        orderAssignments = state.orderAssignments + (nextOrder.id to slabId),
                        slabs = newSlabs,
                        gCost =  newG,
                        hCost =  0
                    )
                )
            }
        }

        // In neuen Slab einfügen
        for (capacity in slabCapacities){
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
                        hCost = 0
                    )
                )
            }
        }
        return successors
    }
}












