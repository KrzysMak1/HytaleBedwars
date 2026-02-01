package com.hytale.bedwars.core.util

class ScheduledTaskRegistry {
    private val tasks = mutableSetOf<CancelableTask>()

    fun register(task: CancelableTask) {
        tasks.add(task)
    }

    fun cancelAll() {
        tasks.forEach { it.cancel() }
        tasks.clear()
    }
}

interface CancelableTask {
    fun cancel()
}

interface TaskScheduler {
    fun schedule(
        delayMillis: Long,
        action: () -> Unit,
    ): CancelableTask

    fun scheduleAtFixedRate(
        initialDelayMillis: Long,
        periodMillis: Long,
        action: () -> Unit,
    ): CancelableTask
}
