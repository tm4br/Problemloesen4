package org.example

import org.example.Heuristics.*
import org.example.Solver.incomplete.BeamSearchSolver
import org.example.VariableSelection.LargestOrderFirst
import org.example.VariableSelection.MostFrequentColorFirst
import org.example.VariableSelection.MostFrequentColorLargestFirst
import org.example.VariableSelection.TightestFitOrderFirst
import kotlin.system.measureTimeMillis

fun main() {

    val filePath = "src/main/resources/111Orders.txt"
    val (orders, _, slabCapacities) = InstanceLoader.load111Order(filePath)

    println("Anzahl Aufträge: ${orders.size}")
    println("Slab-Kapazitäten: $slabCapacities")


    val heuristics = listOf(
        AverageWaste,
        LargestGap,
        GreedyWaste,
        MinSlabs,
        BestFit
    )

    val selectionStrategies = listOf(
        LargestOrderFirst,
        //SmallestOrderFirst,
        TightestFitOrderFirst
    )


    println("Benchmark Steel Mill Slab Problem (111Orders.txt)")
    println("--------------------------------------------------")

    println("%-30s %-30s %-15s %-10s".format("Heuristic", "VariableSelection", "Waste", "Time(ms)"))

    for (heuristic in heuristics) {
        for (selection in selectionStrategies) {
            val solver = BeamSearchSolver(orders, slabCapacities, heuristic, selection)

            var solution: State? = null
            val time = measureTimeMillis {
                solution = solver.solve()
            }

            val waste = solution?.gCost ?: -1
            println("%-30s %-30s %-15d %-10d".format(heuristic::class.simpleName, selection::class.simpleName, waste, time))
        }
    }


    /*//Solver starten
    val solver = Solver(orders, slabCapacities, MinSlabs, MostFrequentColorLargestFirst)

    var solution: State? = null
    val timeMillis = measureTimeMillis {
        solution = solver.solve()
    }

    //Ergebnis ausgeben
    if (solution != null){
        println("Lösung gefunden")
        println("Gesamtkosten (Verschnitt): ${solution!!.fCost()}")
        println("Zuweisungen (orderId -> slabId): ${solution!!.orderAssignments}")

        //Details anzeigen
        for ((slabId, slab) in solution!!.slabs){
            val assigned = slab.assignedOrders.joinToString { "Order(${it.id}, size=${it.size}, color=$it.color" }
            println("Slab $slabId (Capacity: ${slab.capacity}): [$assigned] -> Verschnitt: ${slab.capacity - slab.assignedOrders.sumOf { it.size }}")
        }
    } else {
        println("Keine Lösung gefunden")
    }
    println("Benötigte Zeit: ${timeMillis}ms")*/
}