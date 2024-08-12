package com.bboylin.msgscheduler

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bboylin.messagescheduler.LooperMonitor
import com.bboylin.messagescheduler.TAG
import com.bboylin.msgscheduler.ui.theme.MsgSchedulerTheme

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Splash()
        }
    }

    @Composable
    fun Splash() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "this is Splash Screen , click to Main Screen!",
                modifier = Modifier.clickable {
                    val handler = Handler(Looper.getMainLooper())
                    for (i in 1..10) {
                        handler.post(Runnable {
                            Thread.sleep(200)
                            Log.d(TAG, "SplashActivity sleep 200ms $i time")
                        })
                    }
//                    LooperMonitor.enableLifecycleSchedule()
                    LooperMonitor.start()
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
            )
        }
    }
}