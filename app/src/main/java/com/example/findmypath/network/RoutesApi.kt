package com.example.findmypath.network

import com.example.findmypath.db.RoutePoint
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class RouteUpload(val routeId: String, val points: List<RoutePoint>)

interface RoutesApi {
    @POST("/routes")
    suspend fun uploadRoute(@Body route: RouteUpload): Response<Unit>
}
