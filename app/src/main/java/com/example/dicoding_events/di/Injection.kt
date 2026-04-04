package com.example.dicoding_events.di

import android.content.Context
import com.example.dicoding_events.data.local.EventDatabase
import com.example.dicoding_events.data.remote.ApiConfig
import com.example.dicoding_events.data.repository.EventRepository

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        return EventRepository(apiService, dao)
    }
}

