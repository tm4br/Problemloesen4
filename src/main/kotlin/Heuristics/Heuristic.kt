package org.example.Heuristics

import org.example.Order

interface Heuristic {
    fun estimate(orderAssignments: Map<Int, Int>, order: List<Order>, slabCapacities: List<Int>): Int
}