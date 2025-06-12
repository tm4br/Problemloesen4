package org.example

data class State(
    val orderAssignments: Map<Int,Int>, // orderId -> slabId
    val slabs: Map<Int,Slab>,           // slabId -> Slab
    val gCost: Int,                     // actual cost
    val hCost: Int                      // heuristically estimated cost
){
    fun fCost() = gCost + hCost
}