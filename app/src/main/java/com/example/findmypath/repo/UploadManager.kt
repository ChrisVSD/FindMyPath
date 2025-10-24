package com.example.findmypath.repo

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UploadManager(private val context: Context, private val apiBaseUrl: String, private val apiKey: String? = null) {
    private val repo = RoutesRepository(context, apiBaseUrl, apiKey)

    suspend fun tryUpload(routeId: String): Boolean {
        return repo.uploadRoute(routeId)
    }

    suspend fun retryAllFailed() {
        val failed = repo.getFailedUploads()
        for (f in failed) {
            val ok = tryUpload(f.routeId)
            if (ok) {
                repo.removeFailed(f.id)
            } else {
                // increase attempts - simple logic: remove after 5 attempts
                val attempts = f.attempts + 1
                if (attempts >= 5) {
                    repo.removeFailed(f.id)
                }
            }
        }
    }
}
