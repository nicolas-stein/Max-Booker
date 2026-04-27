package fr.stein.maxbooker.domain.work

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.domain.usecase.SncfApiFetchReservationsUseCase
import fr.stein.maxbooker.domain.utils.NotificationUtils
import kotlinx.coroutines.flow.first

@HiltWorker
class SncfReservationsUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val sncfCustomerDataStore: DataStore<SncfCustomerProto>,
    private val sncfApiFetchReservationsUseCase: SncfApiFetchReservationsUseCase,
    private val objectMapper: ObjectMapper
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        const val WORKER_NAME = "SncfReservationsUpdateWorker"
        const val OUTPUT_DATA_KEY = "sncfReservations"
    }

    override suspend fun doWork(): Result {
        Log.d("Max Book", "SncfReservationsUpdateWorker: doWork()")

        val sncfCustomer = sncfCustomerDataStore.data.first().toDomain()
        if (sncfCustomer == null) {
            Log.e("Max Book", "SncfReservationsUpdateWorker: sncfCustomer is null !")
            return Result.failure()
        }

        return runCatching {
            val output = sncfApiFetchReservationsUseCase(sncfCustomer)
            if (output.newSncfReservations.isNotEmpty()) {
                NotificationUtils.sendNewBookingAddedNotification(
                    applicationContext,
                    output.newSncfReservations
                )
            }

            return Result.success(
                Data.Builder().putString(
                    OUTPUT_DATA_KEY,
                    objectMapper.writeValueAsString(output.newSncfReservations + output.updatedSncfReservations)
                ).build()
            )
        }.onFailure { throwable ->
            Log.e("Max Book", "SncfReservationsUpdateWorker: failed to update sncf reservations", throwable)
        }.getOrElse { Result.failure() }
    }
}
