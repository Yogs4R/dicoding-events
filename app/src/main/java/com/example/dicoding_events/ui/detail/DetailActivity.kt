package com.example.dicoding_events.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.dicoding_events.data.remote.response.ListEventsItem
import com.example.dicoding_events.databinding.ActivityDetailBinding
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import com.example.dicoding_events.R
import com.example.dicoding_events.ui.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var currentEvent: ListEventsItem? = null
    private var isFavorite: Boolean = false
    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)
        if (eventId.isNullOrBlank()) {
            Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        observeFavoriteState(eventId)
        viewModel.getDetailEvent(eventId)
    }

    private fun observeViewModel() {
        viewModel.eventDetail.observe(this) { event ->
            currentEvent = event
            binding.tvDetailName.text = event.name
            binding.tvDetailOwner.text = getString(R.string.organizer_format, event.ownerName)
            binding.tvDetailTime.text = getString(R.string.time_format, event.beginTime)
            val remainingQuota = (event.quota - event.registrant).coerceAtLeast(0)
            binding.tvDetailQuota.text = getString(R.string.quota_format, remainingQuota.toString())
            binding.tvDetailDescription.text = HtmlCompat.fromHtml(
                event.description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            Glide.with(this)
                .load(event.mediaCover)
                .into(binding.ivDetailCover)

            binding.btnOpenLink.setOnClickListener {
                if (event.link.isNotBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW, event.link.toUri())
                    startActivity(intent)
                }
            }

            binding.btnFavorite.setOnClickListener {
                currentEvent?.let { selectedEvent ->
                    if (isFavorite) {
                        viewModel.deleteFavoriteEvent(selectedEvent)
                    } else {
                        viewModel.addFavoriteEvent(selectedEvent)
                    }
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeFavoriteState(eventId: String) {
        viewModel.getFavoriteEventById(eventId).observe(this) { favoriteEvent ->
            isFavorite = favoriteEvent != null
            updateFavoriteButton()
        }
    }

    private fun updateFavoriteButton() {
        val iconRes = if (isFavorite) {
            R.drawable.ic_favorite
        } else {
            R.drawable.ic_favorite_border
        }
        binding.btnFavorite.setImageResource(iconRes)
    }

    companion object {
        const val EXTRA_EVENT_ID = "EVENT_ID"
    }
}


