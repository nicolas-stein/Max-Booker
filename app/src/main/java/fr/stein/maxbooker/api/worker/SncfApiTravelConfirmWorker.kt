package fr.stein.maxbooker.api.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import fr.stein.maxbooker.R
import fr.stein.maxbooker.alarm.TravelConfirmAlarmReceiver
import fr.stein.maxbooker.api.getSncfApi
import fr.stein.maxbooker.database.MaxBookerDatabase
import fr.stein.maxbooker.database.reservations.SncfReservation
import fr.stein.maxbooker.database.reservations.SncfStation
import fr.stein.maxbooker.datastore.MaxBookerDataStore
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class SncfApiTravelConfirmWorker(appContext: Context, workerParameters: WorkerParameters):
    CoroutineWorker(appContext, workerParameters) {

    companion object {
        const val WORK_NAME = "SNCF_API_TRAVEL_CONFIRM_WORK"

        fun showSuccessNotification(context: Context, sncfReservation: SncfReservation, destinationStation: SncfStation) {
            val notificationBuilder = NotificationCompat.Builder(context, TravelConfirmAlarmReceiver.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_maxbooker_notification)
                .setContentTitle("Réservation confirmée")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Votre voyage à ${destinationStation.label.lowercase().replaceFirstChar{it.uppercase()}} le " +
                            "${sncfReservation.departureDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))} à " +
                            "${sncfReservation.departureDateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))} est confirmé !"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.getColor(R.color.notification_success))
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(context).notify(sncfReservation.orderId.hashCode(), notificationBuilder.build())
            }
        }

        fun showErrorNotification(context: Context, orderId: String, destinationStation: SncfStation) {
            val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.maxjeune-tgvinoui.sncf/sncf-connect/mes-voyages"))
            val contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder = NotificationCompat.Builder(context,
                TravelConfirmAlarmReceiver.NOTIFICATION_CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_maxbooker_notification)
                .setContentTitle("Impossible de confirmer la réservation")
                .setContentText("Confirmez manuellement votre voyage à ${destinationStation.label.lowercase().replaceFirstChar{it.uppercase()}} !")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setColor(context.getColor(R.color.notification_error))

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(context).notify(orderId.hashCode(), notificationBuilder.build())
            }
        }
    }

    private val sncfApiSettingsDatastore = MaxBookerDataStore(appContext).getSncfApiSettingsDatastore()
    private val maxBookerDatabase = MaxBookerDatabase.getInstance(applicationContext)

    override suspend fun doWork(): Result {
        Log.d("Max Book", "Running SncfApiTravelDetailsWorker !")

        val orderId = inputData.getString("orderId")
        val origin = inputData.getString("origin")
        val destination = inputData.getString("destination")
        if (orderId == null || origin == null || destination == null) {
            Log.e("Mox Book", "Worker failed to confirm sncf api travel : invalid input ($orderId, $origin, $destination)")
            return Result.failure()
        }

        val destinationStation = maxBookerDatabase.sncfStationDao().get(destination)
        if (SncfApiUserWorker.work(applicationContext).javaClass != Result.success().javaClass) {
            if (SncfApiAuthTokenWorker.work(applicationContext).javaClass != Result.success().javaClass) {
                Log.e("Max Book", "Worker cannot confirm sncf api travel : failed to update sncf auth token")
                showErrorNotification(applicationContext, orderId, destinationStation)
                return Result.failure()
            }
            if (SncfApiUserWorker.work(applicationContext).javaClass != Result.success().javaClass) {
                Log.e("Max Book", "Worker cannot confirm sncf api travel : failed to update sncf user")
                showErrorNotification(applicationContext, orderId, destinationStation)
                return Result.failure()
            }
        }

        if (SncfApiReservationsWorker.work(applicationContext).javaClass != Result.success().javaClass) {
            Log.e("Max Book", "Worker cannot confirm sncf api travel : failed to update sncf reservation")
            showErrorNotification(applicationContext, orderId, destinationStation)
            return Result.failure()
        }

        val sncfReservation = maxBookerDatabase.sncfReservationDao().get(
            orderId = orderId,
            origin = origin,
            destination = destination
        )
        if (sncfReservation == null) {
            Log.e("Mox Book", "Worker failed to confirm sncf api travel : no reservation found with orderId $orderId in database")
            showErrorNotification(applicationContext, orderId, destinationStation)
            return Result.failure()
        }

        if (sncfReservation.travelConfirmed != "TO_BE_CONFIRMED") {
            if (sncfReservation.travelConfirmed == "CONFIRMED") {
                Log.i("Max Book", "Worker cannot confirm sncf api travel : reservation status is already confirmed")
                return Result.success()
            }
            else if (sncfReservation.travelConfirmed == "TOO_LATE_TO_CONFIRM") {
                Log.i("Max Book", "Worker cannot confirm sncf api travel : reservation status is too late to confirm")
                return Result.success()
            }

            Log.e("Max Book", "Worker cannot confirm sncf api travel : reservation status is ${sncfReservation.travelConfirmed} (!= TO_BE_CONFIRMED)")
            showErrorNotification(applicationContext, orderId, destinationStation)
            return Result.failure()
        }

        val sncfApiSettings  = sncfApiSettingsDatastore.data.first()
        val sncfApi = getSncfApi(sncfApiSettings.authToken, sncfApiSettings.cookies)
        val requestBodyJson = JSONObject()
        requestBodyJson.put("marketingCarrierRef", sncfReservation.dvNumber)
        requestBodyJson.put("trainNumber", sncfReservation.trainNumber)
        requestBodyJson.put("departureDateTime", sncfReservation.departureDateTime)

        val apiResponse = try {
            sncfApi.confirmTravel(requestBodyJson.toString()
                .toRequestBody("application/json".toMediaTypeOrNull()))
        } catch (e: Exception) {
            return Result.failure(
                Data.Builder()
                .putBoolean("networkException", true)
                .putString("networkExceptionDetail", e.message).build())
        }

        if(!apiResponse.isSuccessful) {
            Log.e("Mox Book", "Worker failed to confirm sncf api travel : ${apiResponse.code()} ${apiResponse.errorBody()?.string()}")
            showErrorNotification(applicationContext, orderId, destinationStation)
            return Result.failure()
        }

        Log.i("Max Book", "SncfApiTravelDetailsWorker: confirmed reservation successfully !")
        showSuccessNotification(applicationContext, sncfReservation, destinationStation)
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(SncfApiWorkerNotifications.NOTIFICATION_ID_SNCF_API_TRAVEL_CONFIRM_WORKER,
            SncfApiWorkerNotifications.getTravelConfirmWorkerNotification(applicationContext))
    }
}