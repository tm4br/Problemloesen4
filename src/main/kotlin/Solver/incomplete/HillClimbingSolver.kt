package org.example.Solver.incomplete

import org.example.Heuristics.Heuristic
import org.example.Order
import org.example.Slab
import org.example.Solver.BaseSolver
import org.example.State
import org.example.VariableSelection.VariableSelectionHeuristic

class HillClimbingSolver(
    orders: List<Order>,
    slabCapacities: List<Int>,
    private val heuristic: Heuristic,
    private val variableSelectionHeuristic: VariableSelectionHeuristic,
    private val maxIterations: Int = 1000
): BaseSolver(orders, slabCapacities) {

    override fun solve(): State? {
        var current = randomInitialState()
        var currentWaste = current?.gCost ?: Int.MAX_VALUE

        repeat(maxIterations){
            val neighbors = if (current != null) generateSuccessors(current!!) else emptyList()
            val bestNeighbor = neighbors.minByOrNull { it.gCost } ?: return@repeat
            if (bestNeighbor.gCost < currentWaste) {
                current = bestNeighbor
                currentWaste = bestNeighbor.gCost
            }else{
                return current // Keine Verbesserung mehr möglich
            }
        }
        return current
    }

    override fun generateSuccessors(state: State): List<State> {
        val successors = mutableListOf<State>()

        for ((orderId, slabId) in state.orderAssignments){
            for ((newSlabId, slab) in state.slabs) {
                if (newSlabId != slabId){
                    val order = orders.find { it.id == orderId } ?: continue
                    if (canAddOrderToSlab(order, slab)) {
                        val newSlabs = state.slabs.toMutableMap()
                        val oldSlab = newSlabs[slabId]!!.assignedOrders.filter { it.id != orderId }
                        if (oldSlab.isEmpty()) newSlabs.remove(slabId)
                        else newSlabs[slabId] = Slab(state.slabs[slabId]!!.capacity, oldSlab)

                        newSlabs[newSlabId] = slab.copy(assignedOrders = slab.assignedOrders + order)

                        successors.add(
                            State(
                                orderAssignments = state.orderAssignments + (orderId to newSlabId),
                                slabs = newSlabs,
                                gCost = calculateWaste(newSlabs),
                                hCost = heuristic.estimate(
                                    state.orderAssignments + (orderId to newSlabId),
                                    orders,
                                    slabCapacities
                                )
                            )
                        )
                    }
                }
            }
        }
    return successors
    }

    private fun randomInitialState(): State? {
        val shuffledOrders = orders.shuffled()
        val slabs = mutableMapOf<Int, Slab>()
        val assignments = mutableMapOf<Int, Int>()

        for (order in shuffledOrders){
            for (capacity in slabCapacities){
                if (order.size <= capacity){
                    val slabId = (slabs.keys.maxOrNull() ?: -1) + 1
                    slabs[slabId] = Slab(capacity, listOf(order))
                    assignments[order.id] = slabId
                    break
                }
            }
        }
        return State(
            assignments,
            slabs,
            calculateWaste(slabs),
            heuristic.estimate(
                assignments,
                orders,
                slabCapacities
            )
        )
    }
}

