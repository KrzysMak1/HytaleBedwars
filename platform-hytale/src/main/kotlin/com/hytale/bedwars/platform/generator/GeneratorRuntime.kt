package com.hytale.bedwars.platform.generator

import com.hytale.bedwars.core.generator.Generator
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class GeneratorRuntime {
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    fun start(
        generator: Generator,
        dropAction: (Generator) -> Unit,
    ) {
        val intervalMillis = (generator.dropIntervalTicks.coerceAtLeast(1) * 50).toLong()
        scheduler.scheduleAtFixedRate({
            dropAction(generator)
        }, 0, intervalMillis, TimeUnit.MILLISECONDS)
    }

    fun stop() {
        scheduler.shutdown()
    }
}
