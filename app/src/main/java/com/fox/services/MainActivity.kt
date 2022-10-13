package com.fox.services

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.fox.services.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private var page = 0

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
           val binder = (service as? MyForegroundService.LocalBinder) ?: return
            val foregroundService = binder.getService()
            foregroundService.onProgressSetChanged = { progress ->
                Log.d("MainActivity", "$progress")
                binding.progressBarLoading.progress = progress
            }

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
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
//                    .setExtras(MyJobService.newBundle(page++))

                    .setRequiresCharging(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .build()

                val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

                val intent = MyJobService.newIntent(page++)
                val jobWorkItem = JobWorkItem(intent)
                jobScheduler.enqueue(jobInfo, jobWorkItem)
            }

            btnJobIntentService.setOnClickListener {
                MyJobIntentService.enqueue(this@MainActivity, page++)

            }

            btnAlarmManager.setOnClickListener {
                    val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.SECOND, 30)
                val intent = AlarmReceiver.newIntent(this@MainActivity)
                val pendingIntent = PendingIntent.getBroadcast(this@MainActivity, 100, intent, 0 )
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }

            btnWorkManager.setOnClickListener {
                val workManager = WorkManager.getInstance(applicationContext)
                workManager.beginUniqueWork(
                    MyWorker.WORK_NAME,
                    ExistingWorkPolicy.KEEP,
                    MyWorker.makeRequest(page++)
                ).enqueue()
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

    override fun onStart() {
        super.onStart()
        bindService(
            MyForegroundService.newIntent(this@MainActivity),
            serviceConnection,
            0)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
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