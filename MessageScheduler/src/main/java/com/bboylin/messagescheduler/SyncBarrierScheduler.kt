package com.bboylin.messagescheduler

import android.os.Message
import android.os.MessageQueue
import android.util.Log
import java.lang.reflect.Method

class SyncBarrierScheduler : AbsMessageScheduler() {
    private var removeSyncBarrierMethod: Method? = null

    override fun isHighPriorityMessage(message: Message): Boolean {
        return message.target == null
    }

    override fun moveMessageToFrontOfQueue(preMessage: Message, targetMessage: Message) {
        val newBarrier = Message.obtain(targetMessage)
        removeSyncBarrier(newBarrier.arg1)
        handler.sendMessageAtFrontOfQueue(newBarrier)
        newBarrier.target = null
        Log.d(TAG, "SyncBarrierScheduler move SyncBarrier to Front Of Queue")
    }

    private fun removeSyncBarrier(token: Int) {
        if (removeSyncBarrierMethod == null) {
            removeSyncBarrierMethod =
                MessageQueue::class.java.getDeclaredMethod("removeSyncBarrier", Int::class.java)
            removeSyncBarrierMethod!!.isAccessible = true
        }

        removeSyncBarrierMethod!!.invoke(looper.queue, token)
        Log.d(TAG, "removeSyncBarrier success")
    }
}