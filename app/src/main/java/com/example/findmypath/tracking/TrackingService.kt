package com.example.findmypath.tracking

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.findmypath.MainActivity
import com.example.findmypath.R
import com.example.findmypath.repo.RoutesRepository
import com.example.findmypath.db.RoutePoint
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class TrackingService : Service() {

    companion object {
        const val CHANNEL_ID = "tracking_channel_v1"
        const val NOTIF_ID = 12345
        const val ACTION_START = "action_start_tracking"
        const val ACTION_STOP = "action_stop_tracking"
        const val EXTRA_ROUTE_ID = "extra_route_id"
    }

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var repo: RoutesRepository
    private var routeId: String = ""
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        repo = RoutesRepository(applicationContext, getApiBaseUrl())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                for (loc in result.locations) {
                    onNewLocation(loc)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                // start new route if not provided
                routeId = intent.getStringExtra(EXTRA_ROUTE_ID) ?: repo.createRouteId()
                startForeground(NOTIF_ID, buildNotification("Tracking...")) 
                startLocationUpdates()
            }
            ACTION_STOP -> {
                stopLocationUpdates()
                // upload route in background
                scope.launch {
                    try {
                        repo.uploadRoute(routeId)
                    } catch (e: Exception) {
                        android.util.Log.e("TrackingService", "Upload failed: ${'$'}e")
                    }
                }
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startLocationUpdates() {
        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateDistanceMeters(2f)
            .build()
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) return
        fusedClient.requestLocationUpdates(req, locationCallback, mainLooper)
    }

    private fun stopLocationUpdates() { fusedClient.removeLocationUpdates(locationCallback) }

    private fun onNewLocation(location: Location) {
        val ts = System.currentTimeMillis()
        // persist to Room via repository
        scope.launch {
            try {
                repo.savePoint(routeId, location.latitude, location.longitude, ts)
            } catch (e: Exception) {
                android.util.Log.e("TrackingService", "Save failed: ${'$'}e")
            }
        }
        android.util.Log.d("TrackingService", "New location: ${'$'}{location.latitude},${'$'}{location.longitude}")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(CHANNEL_ID, "Tracking", NotificationManager.IMPORTANCE_LOW)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(chan)
        }
    }

    private fun buildNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Find My Path")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun getApiBaseUrl(): String {
        // read from local.properties via system property if provided, else default placeholder
        val env = System.getenv("API_BASE_URL")
        return env ?: "https://api.example.com/"
    }
}
