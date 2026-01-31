package com.hytale.bedwars.core.trap

class TrapQueue(private val maxTraps: Int) {
    private val traps = ArrayDeque<String>()

    fun size(): Int = traps.size

    fun enqueue(trapId: String): Boolean {
        if (traps.size >= maxTraps) {
            return false
        }
        traps.addLast(trapId)
        return true
    }

    fun triggerNext(): String? {
        return if (traps.isEmpty()) null else traps.removeFirst()
    }
}
