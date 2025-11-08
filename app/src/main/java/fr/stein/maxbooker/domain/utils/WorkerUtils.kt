package fr.stein.maxbooker.domain.utils

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import fr.stein.maxbooker.domain.work.SncfReservationsUpdateWorker
import java.util.concurrent.TimeUnit

object WorkerUtils {
    fun scheduleWorkers(context: Context) {
        val workManager = WorkManager.getInstance(context)

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
