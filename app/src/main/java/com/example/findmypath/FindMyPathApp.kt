package com.example.findmypath

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import java.util.concurrent.TimeUnit
import com.example.findmypath.workers.UploadWorker

class FindMyPathApp : Application() {
    override fun onCreate() {
        super.onCreate()
        schedulePeriodicUploadWork()
    }

    private fun schedulePeriodicUploadWork() {
        val apiBase = System.getenv("API_BASE_URL") ?: "https://api.example.com/"
        val apiKey = System.getenv("API_KEY")
        val input = Data.Builder()
            .putString("API_BASE_URL", apiBase)
            .putString("API_KEY", apiKey)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<UploadWorker>(15, TimeUnit.MINUTES)
            .setInputData(input)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("upload-retry-work", ExistingPeriodicWorkPolicy.KEEP, workRequest)
    }
}
