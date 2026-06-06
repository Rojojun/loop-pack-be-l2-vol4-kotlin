package com.loopers.concurrency

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun runConcurrently(count: Int, task: (Int) -> Unit): List<Result<Unit>> {
    val barrier = CyclicBarrier(count + 1)
    val executor = Executors.newFixedThreadPool(count)
    val results = ConcurrentLinkedQueue<Result<Unit>>()
    repeat(count) { index ->
        executor.submit {
            barrier.await()
            results.add(runCatching { task(index) })
        }
    }
    barrier.await()
    executor.shutdown()
    executor.awaitTermination(10, TimeUnit.MINUTES)
    return results.toList()
}
