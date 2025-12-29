package fr.stein.maxbooker.domain.utils

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.stein.maxbooker.domain.repository.sncf.MaxBookerSettingsRepository
import fr.stein.maxbooker.domain.work.SncfReservationsUpdateWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class WorkerScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val maxBookerSettingsRepository: MaxBookerSettingsRepository
) {
    suspend fun scheduleWorkers() {
        Log.i("MaxBooker", "scheduleWorkers: scheduling workers")
        val workManager = WorkManager.getInstance(context)

        val automationsSetting = maxBookerSettingsRepository.automations()

        if (!automationsSetting.isRefreshBookingsDisabledFlow().first()) {
            scheduleSncfReservationsUpdateWorker(workManager)
        }
    }

    fun scheduleSncfReservationsUpdateWorker(workManager: WorkManager) {
        Log.i("MaxBooker", "scheduleWorkers: scheduling SncfReservationsUpdateWorker")
        val sncfReservationsUpdateWorkRequest = PeriodicWorkRequestBuilder<SncfReservationsUpdateWorker>(
            6,
            TimeUnit.HOURS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            SncfReservationsUpdateWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            sncfReservationsUpdateWorkRequest
        )
    }
}
