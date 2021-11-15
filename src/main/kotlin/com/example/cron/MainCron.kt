package com.example.cron

import com.example.models.orderStorage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.util.*

class MainCron {
    init {
        println("Main Cron starting")
    }

    suspend fun orderIncrementor(value: Int) = coroutineScope {
        val timer = Timer()
        val monitor = object: TimerTask() {
            override fun run() {
                println("Incrementing Order prices")
                
                for(order in orderStorage) {
                    for (orderItem in order.contents) {
                        orderItem.price = orderItem.price + value
                    }
                }
            }
        }
        timer.schedule(monitor, 500, 5000)
    }

//    fun orderMultiplier(value: Int) = runBlocking {
//
//    }
}