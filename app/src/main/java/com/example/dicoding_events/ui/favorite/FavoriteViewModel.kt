package com.example.dicoding_events.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dicoding_events.data.local.FavoriteEvent
import com.example.dicoding_events.data.repository.EventRepository

class FavoriteViewModel(
    repository: EventRepository
) : ViewModel() {

    val favoriteEvents: LiveData<List<FavoriteEvent>> = repository.getAllFavoriteEvent()
}

