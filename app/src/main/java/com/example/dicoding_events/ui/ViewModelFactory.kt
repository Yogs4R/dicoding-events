package com.example.dicoding_events.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicoding_events.data.repository.EventRepository
import com.example.dicoding_events.di.Injection
import com.example.dicoding_events.ui.detail.DetailViewModel
import com.example.dicoding_events.ui.favorite.FavoriteViewModel
import com.example.dicoding_events.ui.finished.FinishedViewModel
import com.example.dicoding_events.ui.home.HomeViewModel
import com.example.dicoding_events.ui.upcoming.UpcomingViewModel

class ViewModelFactory private constructor(
    private val repository: EventRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> modelClass.cast(HomeViewModel(repository))
            modelClass.isAssignableFrom(UpcomingViewModel::class.java) -> modelClass.cast(UpcomingViewModel(repository))
            modelClass.isAssignableFrom(FinishedViewModel::class.java) -> modelClass.cast(FinishedViewModel(repository))
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> modelClass.cast(DetailViewModel(repository))
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> modelClass.cast(FavoriteViewModel(repository))
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val repository = Injection.provideRepository(context)
                val instance = ViewModelFactory(repository)
                INSTANCE = instance
                instance
            }
        }
    }
}

