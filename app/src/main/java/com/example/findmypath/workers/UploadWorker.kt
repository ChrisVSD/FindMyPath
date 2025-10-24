package com.example.findmypath.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.findmypath.repo.UploadManager

class UploadWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val apiBase = inputData.getString("API_BASE_URL") ?: System.getenv("API_BASE_URL") ?: "https://api.example.com/"
        val apiKey = inputData.getString("API_KEY") ?: System.getenv("API_KEY")
        val manager = UploadManager(applicationContext, apiBase, apiKey)
        return try {
            manager.retryAllFailed()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
