package com.bboylin.messagescheduler.schedulers

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.MessageQueue
import android.util.Log
import com.bboylin.messagescheduler.MessageDispatchListener
import com.bboylin.messagescheduler.TAG
import java.lang.Exception
import java.lang.reflect.Field

abstract class AbsMessageScheduler : MessageDispatchListener {
    private var enable = false
    private var msgField: Field? = null
    protected var nextField: Field? = null
    protected val looper: Looper = Looper.getMainLooper()
    protected val handler: Handler = Handler(looper)

    fun setEnable(e: Boolean) {
        enable = e
    }

    abstract fun isHighPriorityMessage(message: Message): Boolean

    abstract fun moveMessageToFrontOfQueue(preMessage: Message, targetMessage: Message)

    override fun onDispatchBegin(log: String) {

    }

    override fun onDispatchEnd(log: String) {
        if (!enable) {
            return
        }
        try {
            // move our high priority message to the front of the queue
            val queue = looper.queue
            if (msgField==null) {
                msgField = MessageQueue::class.java.getDeclaredField("mMessages")
                msgField!!.isAccessible = true
            }
            var message: Message? = msgField!!.get(queue) as Message?
            var preMessage: Message? = null
            while (message != null) {
                if (isHighPriorityMessage(message)) {
                    if (preMessage != null) {
                        moveMessageToFrontOfQueue(preMessage, message)
                    }
                    break
                } else {
                    if (nextField==null) {
                        nextField = Message::class.java.getDeclaredField("next")
                        nextField!!.isAccessible = true
                    }
                    preMessage = message
                    message = nextField!!.get(message) as Message?
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "onDispatchEnd error ", e)
        }
    }

}