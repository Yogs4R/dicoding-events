package com.example.dicoding_events.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: FavoriteEvent)

    @Delete
    suspend fun delete(event: FavoriteEvent)

    @Query("SELECT * FROM favorite_event ORDER BY name ASC")
    fun getAllFavoriteEvent(): LiveData<List<FavoriteEvent>>

    @Query("SELECT * FROM favorite_event WHERE id = :id LIMIT 1")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent>
}

