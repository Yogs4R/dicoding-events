package com.example.dicoding_events.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicoding_events.data.remote.response.ListEventsItem
import com.example.dicoding_events.data.repository.EventRepository
import kotlinx.coroutines.launch

class UpcomingViewModel(
    private val repository: EventRepository
) : ViewModel() {

    private val _listEvents = MutableLiveData<List<ListEventsItem>>(emptyList())
    val listEvents: LiveData<List<ListEventsItem>> = _listEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun findEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _listEvents.value = repository.getEvents(1)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

