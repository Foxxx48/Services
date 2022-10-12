package com.fox.services

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.util.Log
import kotlinx.coroutines.*

@SuppressLint("SpecifyJobSchedulerIdRange")
class MyJobService : JobService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }


    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartJob")
//        val page = params?.extras?.getInt(PAGE) ?: 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            coroutineScope.launch {
                var workItem = params?.dequeueWork()
                while (workItem != null) {
                    val page = workItem.intent.getIntExtra(PAGE, 0)

                    for (i in 0 until 5) {
                        delay(1000)
                        log("Timer: $i $page")
                    }
                    params?.completeWork(workItem)
                    workItem = params?.dequeueWork()
                }
                jobFinished(params, false)
            }
        }
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        log("onStopJob")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d(SERVICE_TAG, "My JobService: $message")
    }

    companion object {
        const val JOB_ID = 111
        private const val SERVICE_TAG = "Service_Tag"
        private const val PAGE = "page"

//        fun newBundle(page: Int): PersistableBundle {
//            return PersistableBundle().apply {
//                putInt(PAGE, page)
//            }
//        }

        fun newIntent(page: Int): Intent {
            return Intent().apply {
                putExtra(PAGE, page)
            }
        }
    }
}

