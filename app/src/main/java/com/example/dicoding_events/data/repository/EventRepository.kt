package com.example.dicoding_events.data.repository

import androidx.lifecycle.LiveData
import com.example.dicoding_events.data.local.EventDao
import com.example.dicoding_events.data.local.FavoriteEvent
import com.example.dicoding_events.data.remote.ApiService
import com.example.dicoding_events.data.remote.response.ListEventsItem

class EventRepository(
    private val apiService: ApiService,
    private val eventDao: EventDao
) {

    suspend fun getEvents(active: Int): List<ListEventsItem> {
        return apiService.getEvents(active).listEvents
    }

    suspend fun searchEvents(keyword: String): List<ListEventsItem> {
        return apiService.searchEvents(keyword = keyword).listEvents
    }

    suspend fun getDetailEvent(id: String): ListEventsItem {
        return apiService.getDetailEvent(id).event
    }

    fun getAllFavoriteEvent(): LiveData<List<FavoriteEvent>> = eventDao.getAllFavoriteEvent()

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent> = eventDao.getFavoriteEventById(id)

    suspend fun insertFavoriteEvent(event: FavoriteEvent) {
        eventDao.insert(event)
    }

    suspend fun deleteFavoriteEvent(event: FavoriteEvent) {
        eventDao.delete(event)
    }
}

