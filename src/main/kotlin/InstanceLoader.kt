package org.example

import java.io.File

object InstanceLoader {

    fun load111Order(filePath: String): Triple<List<Order>, Int, List<Int>> {
        val lines = File(filePath).readLines().filter { it.isNotBlank() }

        val slabCapacities = lines[0].split("\\s+".toRegex()).map { it.toInt() }
        val numColors = lines[1].toInt()
        val numOrders = lines[2].toInt()

        val orders = lines.drop(3)
            .take(numOrders)
            .mapIndexed { index, line ->
                val (size, color) = line.trim().split("\\s+".toRegex()).map { it.toInt() }
                Order(index, size, color)
            }
        return Triple(orders, numColors, slabCapacities)
    }

    private fun extractList(fileContent: String, key: String): List<Int> {
        val regex = Regex("$key\\s*=\\s*\\[(.*?)]", RegexOption.DOT_MATCHES_ALL)
        val match = regex.find(fileContent)
            ?: throw IllegalArgumentException("Key '$key' nicht in Datei gefunden.")

        return match.groupValues[1]
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { it.toInt() }
    }
}