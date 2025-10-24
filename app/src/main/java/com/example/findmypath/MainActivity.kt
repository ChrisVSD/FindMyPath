package com.example.findmypath

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    private var polyline: Polyline? = null
    private var tracking = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            if (fine) startTrackingService() else Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val btnToggle = findViewById<FloatingActionButton>(R.id.btnToggleTracking)
        val logo = findViewById<ImageView>(R.id.logo)
        logo.setOnClickListener { Toast.makeText(this, "Find My Path", Toast.LENGTH_SHORT).show() }

        btnToggle.setOnClickListener {
            tracking = !tracking
            btnToggle.setImageResource(if (tracking) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
            if (tracking) startTrackingService() else stopTrackingService()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.uiSettings?.isZoomControlsEnabled = true
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map?.isMyLocationEnabled = true
        }
    }

    private fun startTrackingService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            return
        }
        val intent = Intent(this, tracking.TrackingService::class.java)
        intent.action = tracking.TrackingService.ACTION_START
        startForegroundService(intent)
        Toast.makeText(this, "Tracking started", Toast.LENGTH_SHORT).show()
    }

    private fun stopTrackingService() {
        val intent = Intent(this, tracking.TrackingService::class.java)
        intent.action = tracking.TrackingService.ACTION_STOP
        startService(intent)
        Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show()
    }
}
