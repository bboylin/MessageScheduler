package com.bboylin.messagescheduler

import android.os.Message
import android.util.Log

class ActivityLifecycleScheduler : AbsMessageScheduler() {
    override fun isHighPriorityMessage(message: Message): Boolean {
        return ActivityThreadHacker.isLaunchActivity(message)
    }

    override fun moveMessageToFrontOfQueue(preMessage: Message, targetMessage: Message) {
        nextField.set(preMessage, nextField.get(targetMessage))
        val newMessage = Message.obtain(targetMessage)
        targetMessage.target.sendMessageAtFrontOfQueue(newMessage)
        Log.d(TAG, "ActivityLifecycleScheduler move LAUNCH_ACTIVITY to Front Of Queue")
    }
}