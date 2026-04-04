package com.example.dicoding_events.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicoding_events.data.local.FavoriteEvent
import com.example.dicoding_events.data.remote.response.ListEventsItem
import com.example.dicoding_events.data.repository.EventRepository
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: EventRepository
) : ViewModel() {

    private val _eventDetail = MutableLiveData<ListEventsItem>()
    val eventDetail: LiveData<ListEventsItem> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getDetailEvent(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _eventDetail.value = repository.getDetailEvent(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent> {
        return repository.getFavoriteEventById(id)
    }

    fun addFavoriteEvent(event: ListEventsItem) {
        viewModelScope.launch {
            repository.insertFavoriteEvent(
                FavoriteEvent(
                    id = event.id,
                    name = event.name,
                    mediaCover = event.mediaCover
                )
            )
        }
    }

    fun deleteFavoriteEvent(event: ListEventsItem) {
        viewModelScope.launch {
            repository.deleteFavoriteEvent(
                FavoriteEvent(
                    id = event.id,
                    name = event.name,
                    mediaCover = event.mediaCover
                )
            )
        }
    }
}

