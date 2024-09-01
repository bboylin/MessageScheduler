package com.bboylin.msgscheduler

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.bboylin.messagescheduler.LooperMonitor
import com.bboylin.messagescheduler.TAG

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text(
                text = "Hello MainActivity!"
            )
        }
        Log.d(TAG, "MainActivity onCreate")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity on Resume")
        val handler = Handler(Looper.getMainLooper())
        for (i in 1..10) {
            handler.post(Runnable {
                Thread.sleep(200)
                Log.d(TAG, "MainActivity sleep 200ms $i time")
            })
        }
        LooperMonitor.disableLifecycleSchedule()
        LooperMonitor.enableSyncBarrierSchedule()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            Log.d(TAG, "MainActivity onWindowFocusChanged true")
            LooperMonitor.finish()
        }
    }
}