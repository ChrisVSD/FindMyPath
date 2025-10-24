package com.example.findmypath.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypath.repo.RoutesRepository
import kotlinx.coroutines.launch

class TrackingViewModel(application: Application, apiBaseUrl: String) : AndroidViewModel(application) {
    private val repo = RoutesRepository(application.applicationContext, apiBaseUrl)
    val routeId = repo.createRouteId()

    fun savePoint(lat: Double, lon: Double, ts: Long) {
        viewModelScope.launch { repo.savePoint(routeId, lat, lon, ts) }
    }

    fun uploadRoute() {
        viewModelScope.launch { repo.uploadRoute(routeId) }
    }
}
