package Results

import org.example.Heuristics.AverageWaste
import org.example.Heuristics.GreedyWaste
import org.example.Heuristics.MinSlabs
import org.example.InstanceLoader
import org.example.Solver.incomplete.BeamSearchSolver
import org.example.Solver.incomplete.GreedySearchSolver
import org.example.Solver.incomplete.HillClimbingSolver
import org.example.Solver.incomplete.RandomSearchSolver
import org.example.State
import org.example.VariableSelection.LargestOrderFirst
import org.example.VariableSelection.TightestFitOrderFirst

import kotlin.system.measureTimeMillis

fun main() {
    val filePath = "src/main/resources/111Orders.txt"
    val (orders, _, slabCapacities) = InstanceLoader.load111Order(filePath)

    println("Anzahl Aufträge: ${orders.size}")
    println("Slab-Kapazitäten: $slabCapacities")

    val heuristics = listOf(
        MinSlabs,
        AverageWaste
    )

    val selectionStrategies = listOf(
        LargestOrderFirst,
        TightestFitOrderFirst
    )

    println("Benchmark Incomplete Solvers (111Orders.txt)")
    println("--------------------------------------------------------------")
    println("%-15s %-20s %-25s %-10s %-10s".format("Solver", "Heuristic", "VariableSelection", "Waste", "Time(ms)"))

    var time = 0L
    var solution: State? = null

    // HillClimbing (mit Heuristik, ohne Variablenauswahl)
    for (heuristic in heuristics) {
        val hillSolver = HillClimbingSolver(orders, slabCapacities, heuristic, TightestFitOrderFirst)
        time = 0L
        solution = run {
            var sol: State? = null
            time = measureTimeMillis { sol = hillSolver.solve() }
            sol
        }
        println("%-15s %-20s %-25s %-10d %-10d".format("HillClimbing", heuristic::class.simpleName, "-", solution?.gCost ?: -1, time))
    }

    // GreedySearch (mit Heuristik & Variablenauswahl)
    for (heuristic in heuristics) {
        for (selection in selectionStrategies) {
            val greedySolver = GreedySearchSolver(orders, slabCapacities, heuristic, selection)
            time = 0L
            solution = run {
                var sol: State? = null
                time = measureTimeMillis { sol = greedySolver.solve() }
                sol
            }
            println("%-15s %-20s %-25s %-10d %-10d".format("GreedySearch", heuristic::class.simpleName, selection::class.simpleName, solution?.gCost ?: -1, time))
        }
    }

    // BeamSearch (mit Heuristik & Variablenauswahl)
    for (heuristic in heuristics) {
        for (selection in selectionStrategies) {
            val beamSolver = BeamSearchSolver(orders, slabCapacities, heuristic, selection, beamWidth = 5)
            time = 0L
            solution = run {
                var sol: State? = null
                time = measureTimeMillis { sol = beamSolver.solve() }
                sol
            }
            println("%-15s %-20s %-25s %-10d %-10d".format("BeamSearch", heuristic::class.simpleName, selection::class.simpleName, solution?.gCost ?: -1, time))
        }
    }

    // RandomSearch (ohne Heuristik und Variablenauswahl)
    val randomSolver = RandomSearchSolver(orders, slabCapacities, AverageWaste, TightestFitOrderFirst)
    time = 0L
    solution = run {
        var sol: State? = null
        time = measureTimeMillis { sol = randomSolver.solve() }
        sol
    }
    println("%-15s %-20s %-25s %-10d %-10d".format("RandomSearch", "-", "-", solution?.gCost ?: -1, time))
}
