import org.example.Heuristics.MinSlabs
import org.example.InstanceLoader
import org.example.Solver.incomplete.BeamSearchSolver
import org.example.State
import org.example.VariableSelection.MostFrequentColorLargestFirst
import kotlin.system.measureTimeMillis

fun main() {
    val filePath = "src/main/resources/111Orders.txt"
    val (orders, _, slabCapacities) = InstanceLoader.load111Order(filePath)

    val heuristic = MinSlabs  // oder z. B. GreedyWaste, falls du verschiedene testen willst
    val variableSelection = MostFrequentColorLargestFirst

    val beamWidths = listOf(1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 5000)

    println("BeamSearch Benchmark (111Orders.txt)")
    println("------------------------------------------------------------")
    println("%-15s %-15s %-15s".format("BeamWidth", "Waste", "Time(ms)"))

    for (width in beamWidths) {
        val solver = BeamSearchSolver(
            orders = orders,
            slabCapacities = slabCapacities,
            heuristic = heuristic,
            variableSelectionHeuristic = variableSelection,
            beamWidth = width
        )

        var solution: State? = null
        val time = measureTimeMillis {
            solution = solver.solve()
        }

        val waste = solution?.gCost ?: -1
        println("%-15d %-15d %-15d".format(width, waste, time))
    }
}
