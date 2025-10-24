package com.example.findmypath.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "failed_uploads")
data class FailedUpload(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routeId: String,
    val attempts: Int = 0,
    val lastError: String? = null
)
