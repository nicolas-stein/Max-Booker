package fr.stein.maxbooker.domain.work

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import fr.stein.maxbooker.data.local.database.SncfReservationDao
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.usecase.SncfApiConfirmTravelUseCase
import fr.stein.maxbooker.domain.usecase.SncfApiFetchReservationsUseCase
import fr.stein.maxbooker.domain.utils.NotificationUtils
import fr.stein.maxbooker.domain.utils.NotificationUtils.sendBookingConfirmedFailedNotification
import kotlinx.coroutines.flow.first

@HiltWorker
class SncfReservationConfirmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val sncfCustomerDataStore: DataStore<SncfCustomerProto>,
    private val sncfReservationDao: SncfReservationDao,
    private val sncfApiFetchReservationsUseCase: SncfApiFetchReservationsUseCase,
    private val sncfApiConfirmTravelUseCase: SncfApiConfirmTravelUseCase
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        val WORKER_TAG = "SncfReservationConfirmWorker"
        fun getWorkerName(sncfReservation: SncfReservation): String =
            "SncfReservationConfirmWorker-${sncfReservation.dvNumber}-${sncfReservation.trainNumber}"
    }

    override suspend fun doWork(): Result {
        Log.d("Max Book", "SncfReservationConfirmWorker: doWork() with params ${workerParams.inputData}")
        val sncfReservationDvNumber = workerParams.inputData.getString("dvNumber")
        val sncfReservationTrainNumber = workerParams.inputData.getString("trainNumber")
        if (sncfReservationDvNumber == null) {
            Log.e("Max Book", "SncfReservationConfirmWorker: input parameters missing dvNumber")
            return Result.failure()
        } else if (sncfReservationTrainNumber == null) {
            Log.e("Max Book", "SncfReservationConfirmWorker: input parameters missing trainNumber")
            return Result.failure()
        }

        val sncfCustomer = sncfCustomerDataStore.data.first().toDomain()
        if (sncfCustomer == null) {
            Log.e("Max Book", "SncfReservationConfirmWorker: sncfCustomer is null")
            return Result.failure()
        }

        val sncfReservation: SncfReservation = runCatching {
            return@runCatching sncfApiFetchReservationsUseCase(sncfCustomer).updatedSncfReservations.first {
                it.dvNumber == sncfReservationDvNumber && it.trainNumber == sncfReservationTrainNumber
            }
        }.getOrElse { throwable ->
            Log.e("Max Book", "SncfReservationConfirmWorker: failed to fetch sncf reservation", throwable)
            val sncfReservationLocal = sncfReservationDao.getReservation(sncfReservationDvNumber, sncfReservationTrainNumber)?.toDomain()
            if (sncfReservationLocal != null) {
                sendBookingConfirmedFailedNotification(applicationContext, sncfReservationLocal)
            }
            return Result.retry()
        }

        if (sncfReservation.travelConfirmed != "TO_BE_CONFIRMED") {
            if (sncfReservation.travelConfirmed == "CONFIRMED") {
                Log.i(
                    "Max Book",
                    "SncfReservationConfirmWorker: cannot confirm sncf reservation, status is already confirmed"
                )
                return Result.success()
            } else if (sncfReservation.travelConfirmed == "TOO_LATE_TO_CONFIRM") {
                Log.i(
                    "Max Book",
                    "SncfReservationConfirmWorker: cannot confirm sncf reservation, status is too late to confirmed"
                )
                return Result.success()
            }

            Log.e(
                "Max Book",
                "SncfReservationConfirmWorker: cannot confirm sncf reservation, status is ${sncfReservation.travelConfirmed} (!= TO_BE_CONFIRMED)"
            )
            return Result.failure()
        }

        Log.i("Max Book", "SncfReservationConfirmWorker: confirming sncf reservation ${sncfReservation.dvNumber}-${sncfReservation.trainNumber}")
        return runCatching {
            sncfApiConfirmTravelUseCase.invoke(sncfReservation)
            NotificationUtils.sendBookingConfirmedNotification(applicationContext, sncfReservation)
            runCatching { sncfApiFetchReservationsUseCase(sncfCustomer) }
            return Result.success()
        }.onFailure { throwable ->
            Log.e("Max Book", "SncfReservationConfirmWorker: failed to confirm sncf reservations", throwable)
            sendBookingConfirmedFailedNotification(applicationContext, sncfReservation)
        }.getOrElse { Result.retry() }
    }
}
