package com.fox.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fox.services.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnSimpleService.setOnClickListener {
                startService(MyService.newIntent(this@MainActivity, 25))
            }

            btnForegroundService.setOnClickListener {
                ContextCompat.startForegroundService(
                    this@MainActivity,
                    MyForegroundService.newIntent(this@MainActivity)
                )
//                showNotification()
            }

            btnIntentService.setOnClickListener {
//                ContextCompat.startForegroundService(this@MainActivity,MyIntentService.newIntent(this@MainActivity))
                startService(MyIntentService.newIntent(this@MainActivity))
            }

            btnJobScheduler.setOnClickListener {
                val componentName = ComponentName(this@MainActivity, MyJobService::class.java)
                val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
                    .setRequiresCharging(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .build()

                val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
                jobScheduler.schedule(jobInfo)
            }

            btnJobIntentService.setOnClickListener {

            }

            btnAlarmManager.setOnClickListener {

            }

            btnWorkManager.setOnClickListener {

            }

            btnStop.setOnClickListener {
                stopService(MyService.newIntent(this@MainActivity,1))
                stopService(MyForegroundService.newIntent(this@MainActivity))
                stopService(MyIntentService.newIntent(this@MainActivity))
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                notificationManager.createNotificationChannel(notificationChannel)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Text")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()


        notificationManager.notify(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"

    }
}