import org.example.Heuristics.MinSlabs
import org.example.InstanceLoader
import org.example.Solver.incomplete.HillClimbingSolver
import org.example.State
import org.example.VariableSelection.TightestFitOrderFirst
import kotlin.system.measureTimeMillis

fun main() {
    val filePath = "src/main/resources/111Orders.txt"
    val (orders, _, slabCapacities) = InstanceLoader.load111Order(filePath)

    val heuristic = MinSlabs
    val maxIterationsList = listOf(10, 50, 100, 500, 1000, 5000, 10000, 20000, 50000)

    println("HillClimbing Benchmark (111Orders.txt)")
    println("------------------------------------------------------------")
    println("%-20s %-15s %-15s".format("MaxIterations", "Waste", "Time(ms)"))

    for (iterations in maxIterationsList) {
        val solver = HillClimbingSolver(
            orders = orders,
            slabCapacities = slabCapacities,
            heuristic = heuristic,
            variableSelectionHeuristic = TightestFitOrderFirst,
            maxIterations = iterations
        )

        var solution: State? = null
        val time = measureTimeMillis {
            solution = solver.solve()
        }

        val waste = solution?.gCost ?: -1
        println("%-20d %-15d %-15d".format(iterations, waste, time))
    }
}
