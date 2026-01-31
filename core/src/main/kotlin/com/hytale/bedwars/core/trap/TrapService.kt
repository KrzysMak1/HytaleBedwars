package com.hytale.bedwars.core.trap

data class TrapEffect(val alarmMessage: String, val debuff: String, val revealTag: String)

class TrapService {
    fun trigger(queue: TrapQueue, intruderName: String): TrapEffect? {
        val trapId = queue.triggerNext() ?: return null
        return TrapEffect(
            alarmMessage = "Trap triggered by $intruderName",
            debuff = trapId,
            revealTag = "reveal-$intruderName",
        )
    }
}
