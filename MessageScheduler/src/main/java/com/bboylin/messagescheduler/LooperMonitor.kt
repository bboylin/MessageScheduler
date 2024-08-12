package com.bboylin.messagescheduler

import android.os.Looper
import android.util.Log
import android.util.Printer
import java.lang.RuntimeException
import java.util.function.Consumer


const val TAG = "LooperMonitor"

class LooperMonitor() {
    private val dispatchListeners: MutableList<MessageDispatchListener> = ArrayList()
    private val looper: Looper = Looper.getMainLooper()
    private var originPrinter: Printer? = null


    private fun register(listener: MessageDispatchListener) {
        synchronized(this) {
            dispatchListeners.add(listener)
        }
    }

    private fun unregister(listener: MessageDispatchListener) {
        synchronized(this) {
            dispatchListeners.remove(listener)
        }
    }

    private fun start() {
        replacePrinter()
    }

    private fun finish() {
        recoverPrinter()
        dispatchListeners.clear()
    }

    private fun recoverPrinter() {
        originPrinter?.let {
            looper.setMessageLogging(originPrinter)
        }
    }

    private fun replacePrinter() {
        val prop = Looper::class.java.getDeclaredField("mLogging")
        prop.isAccessible = true
        originPrinter = prop.get(looper) as Printer?
        looper.setMessageLogging(LooperPrinter(originPrinter))
        if (originPrinter == null) {
            Log.w(TAG, "originPrinter is null")
        } else {
            Log.d(TAG, "originPrinter is not null")
        }
    }

    companion object {
        private val monitor = LooperMonitor()
        private val lifecycleScheduler = ActivityLifecycleScheduler()
        private val syncBarrierScheduler = SyncBarrierScheduler()

        fun enableLifecycleSchedule() {
            lifecycleScheduler.setEnable(true)
        }

        fun disableLifecycleSchedule() {
            lifecycleScheduler.setEnable(false)
        }

        fun enableSyncBarrierSchedule() {
            syncBarrierScheduler.setEnable(true)
        }

        fun disableSyncBarrierSchedule() {
            syncBarrierScheduler.setEnable(false)
        }

        fun start() {
            monitor.register(lifecycleScheduler)
            monitor.register(syncBarrierScheduler)
            monitor.register(object : MessageDispatchListener {
                override fun onDispatchEnd(log: String) {
                    super.onDispatchEnd(log)
                }

                override fun onDispatchBegin(log: String) {
                    Log.d(TAG, log)
                }
            })
            monitor.start()
        }

        fun finish() {
            disableLifecycleSchedule()
            disableSyncBarrierSchedule()
            monitor.finish()
        }
    }

    private inner class LooperPrinter(private val originPrinter: Printer?) : Printer {

        override fun println(log: String) {
            originPrinter?.println(log)
            if (log[0] == '>') {
                dispatchBegin(log)
            } else if (log[0] == '<') {
                dispatchEnd(log)
            } else {
                Log.d(TAG, "not dispatching Msg")
            }
        }

        private fun dispatchBegin(log: String) {
            synchronized(this@LooperMonitor) {
                dispatchListeners.forEach(Consumer { dispatchListener ->
                    dispatchListener.onDispatchBegin(
                        log
                    )
                })
            }
        }

        private fun dispatchEnd(log: String) {
            synchronized(this@LooperMonitor) {
                dispatchListeners.forEach(Consumer { dispatchListener ->
                    dispatchListener.onDispatchEnd(
                        log
                    )
                })
            }
        }
    }
}