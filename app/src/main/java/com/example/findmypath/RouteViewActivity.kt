package com.example.findmypath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.findmypath.db.RouteDatabase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RouteViewActivity : AppCompatActivity(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    private var routeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_view)
        routeId = intent.getStringExtra("routeId")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragmentView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        lifecycleScope.launch {
            val points = withContext(Dispatchers.IO) {
                RouteDatabase.getInstance(applicationContext).routeDao().getByRouteId(routeId ?: "")
            }
            if (points.isNotEmpty()) {
                val latLngs = points.map { LatLng(it.latitude, it.longitude) }
                map?.addPolyline(PolylineOptions().addAll(latLngs))
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs[0], 16f))
            }
        }
    }
}
