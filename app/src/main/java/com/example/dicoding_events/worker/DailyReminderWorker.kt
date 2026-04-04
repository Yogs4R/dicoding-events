package com.example.dicoding_events.worker

import android.Manifest
import android.app.PendingIntent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dicoding_events.R
import com.example.dicoding_events.data.remote.ApiConfig
import com.example.dicoding_events.data.remote.response.ListEventsItem
import com.example.dicoding_events.ui.detail.DetailActivity

class DailyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val response = ApiConfig.getApiService().getEvents(active = 1)
            val latestActiveEvent = response.listEvents.lastOrNull() ?: return Result.success()
            showNotification(latestActiveEvent)
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(event: ListEventsItem) {
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val detailIntent = Intent(applicationContext, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
        }

        val pendingIntent = TaskStackBuilder.create(applicationContext)
            .addNextIntentWithParentStack(detailIntent)
            .getPendingIntent(
                event.id.hashCode(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            ?: return

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_event)
            .setContentTitle(applicationContext.getString(R.string.daily_reminder))
            .setContentText(event.name)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                applicationContext.getString(R.string.daily_reminder),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily notification for nearest upcoming event"
            }

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val UNIQUE_WORK_NAME = "daily_reminder_work"
        private const val CHANNEL_ID = "daily_reminder_channel"
        private const val NOTIFICATION_ID = 1001
    }
}


