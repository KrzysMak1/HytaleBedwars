package com.hytale.bedwars.core.generator

import com.hytale.bedwars.core.api.WorldBridge
import com.hytale.bedwars.core.economy.Currency
import com.hytale.bedwars.core.map.MapTemplate
import com.hytale.bedwars.core.match.Match
import com.hytale.bedwars.core.match.MatchConfig
import com.hytale.bedwars.core.util.TaskScheduler
import java.util.concurrent.ConcurrentHashMap

class GeneratorEngine {
    private val activeGenerators = mutableListOf<Generator>()
    private val counts = ConcurrentHashMap<String, Int>()
    private val tasks = mutableListOf<com.hytale.bedwars.core.util.CancelableTask>()

    fun start(
        match: Match,
        map: MapTemplate,
        config: MatchConfig,
        scheduler: TaskScheduler,
        worldBridge: WorldBridge,
    ) {
        stop()
        activeGenerators.clear()
        activeGenerators.addAll(
            map.generators.map { template ->
                Generator(
                    id = "${map.id}-${template.type}-${template.location.x}-${template.location.z}",
                    type = template.type,
                    dropIntervalTicks = defaultInterval(template.type),
                    cap = config.maxItemsOnGround,
                    mergeRadius = config.mergeRadius,
                    teamColor = template.teamColor,
                )
            },
        )
        match.generators.clear()
        match.generators.addAll(activeGenerators)
        activeGenerators.forEach { generator ->
            val intervalMillis = (generator.dropIntervalTicks.coerceAtLeast(1) * 50).toLong()
            val task =
                scheduler.scheduleAtFixedRate(0, intervalMillis) {
                    val current = counts.getOrDefault(generator.id, 0)
                    if (current >= generator.cap) {
                        return@scheduleAtFixedRate
                    }
                    val mapGenerator =
                        map.generators.firstOrNull { template ->
                            template.type == generator.type && template.teamColor == generator.teamColor
                        } ?: return@scheduleAtFixedRate
                    worldBridge.dropItem(mapGenerator.location, generator.type.name.lowercase(), 1, generator.mergeRadius)
                    counts[generator.id] = current + 1
                }
            tasks.add(task)
            match.scheduledTasks.register(task)
        }
    }

    fun stop() {
        tasks.forEach { it.cancel() }
        tasks.clear()
        counts.clear()
        activeGenerators.clear()
    }

    private fun defaultInterval(type: Currency): Int {
        return when (type) {
            Currency.IRON -> 40
            Currency.GOLD -> 80
            Currency.DIAMOND -> 200
            Currency.EMERALD -> 400
        }
    }
}
