package com.example.findmypath.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_points")
data class RoutePoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routeId: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
