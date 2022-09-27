package com.fox.services

import android.content.Context
import android.util.Log
import androidx.work.*

class MyWorker(
    context: Context,
    private val workerParameters: WorkerParameters
) : Worker(
    context,
    workerParameters
) {
    override fun doWork(): Result {
        log("doWork")
        val page = workerParameters.inputData.getInt(PAGE, 0)
        for (i in 0 until 10) {
            Thread.sleep(1000)
            log("Timer: $i $page")
        }
        return Result.success()
    }

    private fun log(message: String) {
        Log.d(SERVICE_TAG, "My Worker: $message")
    }

    companion object {

        private const val SERVICE_TAG = "Service_Tag"
        private const val PAGE = "page"
        const val WORK_NAME = "work_mame"

        fun makeRequest(page:Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<MyWorker>()
                .setInputData(workDataOf(PAGE to page))
                .setConstraints(makeConstraints())
                .build()
        }

        private fun makeConstraints(): Constraints {
            return Constraints.Builder()
                .setRequiresCharging(true)
                .build()
        }
    }
}