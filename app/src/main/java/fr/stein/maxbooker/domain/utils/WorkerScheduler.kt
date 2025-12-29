package fr.stein.maxbooker.domain.utils

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.stein.maxbooker.data.local.database.SncfReservationDao
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.domain.repository.sncf.MaxBookerSettingsRepository
import fr.stein.maxbooker.domain.work.SncfReservationConfirmWorker
import fr.stein.maxbooker.domain.work.SncfReservationsUpdateWorker
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class WorkerScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val maxBookerSettingsRepository: MaxBookerSettingsRepository,
    private val sncfReservationDao: SncfReservationDao
) {
    suspend fun scheduleWorkers() {
        Log.i("Max Book", "scheduleWorkers: scheduling workers")
        val workManager = WorkManager.getInstance(context)

        val automationsSetting = maxBookerSettingsRepository.automations()

        if (!automationsSetting.isRefreshBookingsDisabledFlow().first()) {
            scheduleSncfReservationsUpdateWorker(workManager)
        }

        if (!automationsSetting.isAutoConfirmBookingsDisabledFlow().first()) {
            scheduleSncfReservationConfirmWorker(workManager)
        }
    }

    fun scheduleSncfReservationsUpdateWorker(workManager: WorkManager) {
        Log.i("Max Book", "WorkerScheduler: scheduling SncfReservationsUpdateWorker")
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

    suspend fun scheduleSncfReservationConfirmWorker(workManager: WorkManager) {
        Log.i("Max Book", "WorkerScheduler: scheduling SncfReservationConfirmWorker")
        val sncfReservations = sncfReservationDao.getAllReservations()
        sncfReservations.map { it.toDomain() }.filter { sncfReservation ->
            sncfReservation.departureDateTime.isAfter(ZonedDateTime.now()) &&
                sncfReservation.travelConfirmed !in arrayOf("TOO_LATE_TO_CONFIRM", "CONFIRMED")
        }.forEach { sncfReservation ->
            val sncfReservationConfirmWorkerRequestBuilder = OneTimeWorkRequestBuilder<SncfReservationConfirmWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.MINUTES)
                .setInputData(
                    Data.Builder()
                        .putString("orderId", sncfReservation.orderId)
                        .build()
                )
                .addTag(SncfReservationConfirmWorker.WORKER_TAG)

            val timeToReservation = Duration.between(
                ZonedDateTime.now(),
                sncfReservation.departureDateTime.minusHours(47)
            )
            if (!timeToReservation.isNegative) {
                sncfReservationConfirmWorkerRequestBuilder.setInitialDelay(timeToReservation)
            }

            workManager.enqueueUniqueWork(
                SncfReservationConfirmWorker.getWorkerName(sncfReservation),
                ExistingWorkPolicy.REPLACE,
                sncfReservationConfirmWorkerRequestBuilder.build()
            )
        }
    }
}
