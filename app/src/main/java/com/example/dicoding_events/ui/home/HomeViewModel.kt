package com.example.dicoding_events.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicoding_events.data.remote.response.ListEventsItem
import com.example.dicoding_events.data.repository.EventRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: EventRepository
) : ViewModel() {

    private val _listUpcoming = MutableLiveData<List<ListEventsItem>>(emptyList())
    val listUpcoming: LiveData<List<ListEventsItem>> = _listUpcoming

    private val _listFinished = MutableLiveData<List<ListEventsItem>>(emptyList())
    val listFinished: LiveData<List<ListEventsItem>> = _listFinished

    private val _listSearch = MutableLiveData<List<ListEventsItem>>(emptyList())
    val listSearch: LiveData<List<ListEventsItem>> = _listSearch

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun findUpcomingEvents() {
        requestEvents(active = 1) { events ->
            _listUpcoming.value = events
        }
    }

    fun findFinishedEvents() {
        requestEvents(active = 0) { events ->
            _listFinished.value = events
        }
    }

    fun searchEvents(keyword: String) {
        requestEvents(active = -1, keyword = keyword) { events ->
            _listSearch.value = events
        }
    }

    private fun requestEvents(
        active: Int,
        keyword: String = "",
        onSuccess: (List<ListEventsItem>) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val events = if (keyword.isBlank()) {
                    repository.getEvents(active)
                } else {
                    repository.searchEvents(keyword)
                }
                onSuccess(events)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

