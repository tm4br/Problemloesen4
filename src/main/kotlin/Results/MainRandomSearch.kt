import org.example.Heuristics.MinSlabs
import org.example.InstanceLoader
import org.example.Solver.incomplete.RandomSearchSolver
import org.example.State
import org.example.VariableSelection.LargestOrderFirst
import kotlin.system.measureTimeMillis

fun main() {
    val filePath = "src/main/resources/111Orders.txt"
    val (orders, _, slabCapacities) = InstanceLoader.load111Order(filePath)

    val heuristic = MinSlabs  // Für RandomSearch: beliebige Heuristik, wird aber nicht „geführt“
    val variableSelection = LargestOrderFirst // Für RandomSearch: keine echte Bedeutung, aber nötig zur Signatur

    val iterationValues = listOf(10, 50, 100, 300, 500, 1000, 5000, 10000)

    println("RandomSearch Benchmark (111Orders.txt)")
    println("------------------------------------------------------------")
    println("%-15s %-15s %-15s".format("Iterations", "Waste", "Time(ms)"))

    for (iterations in iterationValues) {
        val solver = RandomSearchSolver(
            orders = orders,
            slabCapacities = slabCapacities,
            heuristic = heuristic,
            variableSelection = variableSelection,
            maxIterations = iterations
        )

        var solution: State? = null
        val time = measureTimeMillis {
            solution = solver.solve()
        }

        val waste = solution?.gCost ?: -1
        println("%-15d %-15d %-15d".format(iterations, waste, time))
    }
}
