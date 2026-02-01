package com.hytale.bedwars.platform.util

import com.hytale.bedwars.core.util.CancelableTask
import com.hytale.bedwars.core.util.TaskScheduler
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class ExecutorTaskScheduler(
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(2),
) : TaskScheduler {
    override fun schedule(
        delayMillis: Long,
        action: () -> Unit,
    ): CancelableTask {
        val future = executor.schedule(action, delayMillis, TimeUnit.MILLISECONDS)
        return FutureTask(future)
    }

    override fun scheduleAtFixedRate(
        initialDelayMillis: Long,
        periodMillis: Long,
        action: () -> Unit,
    ): CancelableTask {
        val future = executor.scheduleAtFixedRate(action, initialDelayMillis, periodMillis, TimeUnit.MILLISECONDS)
        return FutureTask(future)
    }

    fun shutdown() {
        executor.shutdown()
    }

    private class FutureTask(private val future: ScheduledFuture<*>) : CancelableTask {
        override fun cancel() {
            future.cancel(false)
        }
    }
}
