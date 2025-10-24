package com.example.findmypath.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RouteDao {
    @Insert
    suspend fun insert(point: RoutePoint)

    @Query("SELECT * FROM route_points WHERE routeId = :routeId ORDER BY timestamp ASC")
    suspend fun getByRouteId(routeId: String): List<RoutePoint>

    @Query("SELECT DISTINCT routeId FROM route_points ORDER BY timestamp DESC")
    suspend fun getAllRouteIds(): List<String>
}
