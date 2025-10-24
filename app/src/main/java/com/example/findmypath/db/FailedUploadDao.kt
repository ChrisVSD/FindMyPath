package com.example.findmypath.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FailedUploadDao {
    @Insert
    suspend fun insert(failed: FailedUpload)

    @Query("SELECT * FROM failed_uploads ORDER BY id ASC")
    suspend fun getAll(): List<FailedUpload>

    @Update
    suspend fun update(failed: FailedUpload)

    @Query("DELETE FROM failed_uploads WHERE id = :id")
    suspend fun deleteById(id: Long)
}
