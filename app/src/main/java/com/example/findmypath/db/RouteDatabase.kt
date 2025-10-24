package com.example.findmypath.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RoutePoint::class, FailedUpload::class], version = 2)
abstract class RouteDatabase : RoomDatabase() {
    abstract fun routeDao(): RouteDao
    abstract fun failedUploadDao(): FailedUploadDao

    companion object {
        @Volatile private var INSTANCE: RouteDatabase? = null
        fun getInstance(context: Context): RouteDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(context.applicationContext, RouteDatabase::class.java, "routes.db")
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
