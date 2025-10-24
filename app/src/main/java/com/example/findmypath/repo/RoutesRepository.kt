package com.example.findmypath.repo

import android.content.Context
import com.example.findmypath.db.FailedUpload
import com.example.findmypath.db.RouteDatabase
import com.example.findmypath.db.RoutePoint
import com.example.findmypath.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class RoutesRepository(private val context: Context, private val apiBaseUrl: String, private val apiKey: String? = null) {
    private val db = RouteDatabase.getInstance(context)
    private val api = NetworkModule.create(apiBaseUrl, apiKey)

    suspend fun savePoint(routeId: String, lat: Double, lon: Double, ts: Long) {
        withContext(Dispatchers.IO) {
            db.routeDao().insert(RoutePoint(routeId = routeId, latitude = lat, longitude = lon, timestamp = ts))
        }
    }

    suspend fun uploadRoute(routeId: String) : Boolean {
        return withContext(Dispatchers.IO) {
            val points = db.routeDao().getByRouteId(routeId)
            val upload = com.example.findmypath.network.RouteUpload(routeId, points)
            try {
                val resp = api.uploadRoute(upload)
                resp.isSuccessful
            } catch (e: Exception) {
                // On exception, add to failed uploads
                try {
                    db.failedUploadDao().insert(FailedUpload(routeId = routeId, attempts = 0, lastError = e.message))
                } catch (_: Exception) {}
                false
            }
        }
    }

    suspend fun recordFailed(routeId: String, attempts: Int, lastError: String?) {
        withContext(Dispatchers.IO) {
            db.failedUploadDao().insert(FailedUpload(routeId = routeId, attempts = attempts, lastError = lastError))
        }
    }

    suspend fun getFailedUploads() = withContext(Dispatchers.IO) { db.failedUploadDao().getAll() }

    suspend fun removeFailed(id: Long) = withContext(Dispatchers.IO) { db.failedUploadDao().deleteById(id) }

    fun createRouteId(): String = UUID.randomUUID().toString()
}
