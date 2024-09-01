package com.bboylin.messagescheduler

import android.os.Build
import android.os.Message
import android.util.Log
import java.lang.reflect.Method

const val LAUNCH_ACTIVITY = 100
const val RELAUNCH_ACTIVITY = 126

// for Android 9.0
const val EXECUTE_TRANSACTION = 159

class ActivityThreadHacker {
    companion object {
        private var method: Method? = null

        fun isLaunchActivity(msg: Message): Boolean {
            return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                if (msg.what == EXECUTE_TRANSACTION && msg.obj != null) {
                    try {
                        if (null == method) {
                            val clazz =
                                Class.forName("android.app.servertransaction.ClientTransaction")
                            method = clazz.getDeclaredMethod("getCallbacks")
                            method!!.isAccessible = true
                        }
                        val list = method!!.invoke(msg.obj) as List<*>
                        if (list.isNotEmpty()) {
                            val isLaunchActivity =
                                list[0]!!.javaClass.name.endsWith(".LaunchActivityItem")
                            if (isLaunchActivity) {
                                Log.d(TAG, "isLaunchActivity")
                            }
                            return isLaunchActivity
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "[ActivityThreadHacker isLaunchActivity] %s", e)
                    }
                }
                msg.what == LAUNCH_ACTIVITY
            } else {
                msg.what == LAUNCH_ACTIVITY || msg.what == RELAUNCH_ACTIVITY
            }
        }
    }
}