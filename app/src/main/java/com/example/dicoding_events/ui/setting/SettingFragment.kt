package com.example.dicoding_events.ui.setting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dicoding_events.data.preference.SettingPreferences
import com.example.dicoding_events.databinding.FragmentSettingBinding
import com.example.dicoding_events.worker.DailyReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingViewModel: SettingViewModel

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scheduleDailyReminder()
            settingViewModel.saveReminderSetting(true)
        } else {
            cancelDailyReminder()
            settingViewModel.saveReminderSetting(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = SettingPreferences.getInstance(requireContext())
        settingViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(pref)
        )[SettingViewModel::class.java]

        observeThemeSetting()
        observeReminderSetting()
    }

    private fun observeThemeSetting() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingViewModel.getThemeSettings().collect { isDarkModeActive ->
                    binding.switchTheme.setOnCheckedChangeListener(null)
                    binding.switchTheme.isChecked = isDarkModeActive
                    binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
                        settingViewModel.saveThemeSetting(isChecked)
                    }

                    AppCompatDelegate.setDefaultNightMode(
                        if (isDarkModeActive) {
                            AppCompatDelegate.MODE_NIGHT_YES
                        } else {
                            AppCompatDelegate.MODE_NIGHT_NO
                        }
                    )
                }
            }
        }
    }

    private fun observeReminderSetting() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingViewModel.getReminderSettings().collect { isReminderActive ->
                    binding.switchReminder.setOnCheckedChangeListener(null)
                    binding.switchReminder.isChecked = isReminderActive
                    binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            enableReminder()
                        } else {
                            cancelDailyReminder()
                            settingViewModel.saveReminderSetting(false)
                        }
                    }
                }
            }
        }
    }

    private fun enableReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        scheduleDailyReminder()
        settingViewModel.saveReminderSetting(true)
    }

    private fun scheduleDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            DailyReminderWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun cancelDailyReminder() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork(DailyReminderWorker.UNIQUE_WORK_NAME)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


