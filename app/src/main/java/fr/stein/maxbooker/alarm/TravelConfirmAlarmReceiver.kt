package fr.stein.maxbooker.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import fr.stein.maxbooker.R
import fr.stein.maxbooker.api.worker.SncfApiTravelConfirmWorker
import fr.stein.maxbooker.database.MaxBookerDatabase
import fr.stein.maxbooker.database.reservations.SncfReservation
import fr.stein.maxbooker.database.reservations.SncfStation
import fr.stein.maxbooker.datastore.MaxBookerDataStore
import kotlinx.coroutines.flow.first


class TravelConfirmAlarmReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_RESERVATIONS"

        fun registerNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return
            }

            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Réservations", NotificationManager.IMPORTANCE_HIGH)

            channel.description = "Notifications liés au réservations"
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) = goAsync {
        Log.i("Max Book", "TravelConfirmAlarmReceiver triggered !")
        if (context == null || intent == null || !intent.hasExtra("orderId")) {
            Log.e("Max Book", "TravelConfirmAlarmReceiver context or intent or orderId null !")
            return@goAsync
        }
        val maxBookerDatabase = MaxBookerDatabase.getInstance(context)
        val sncfReservation = maxBookerDatabase.sncfReservationDao().get(
            orderId = intent.getStringExtra("orderId")!!,
            origin = intent.getStringExtra("origin")!!,
            destination = intent.getStringExtra("destination")!!)
        if (sncfReservation == null) {
            Log.e("Max Book", "TravelConfirmAlarmReceiver : sncf reservation not found in database !")
            return@goAsync
        }
        val myReservationsSettings = MaxBookerDataStore(context).getAppSettingsDatastore().data.first().myReservationsSettings

        if (myReservationsSettings.autoConfirmReservations) {
            startAutoConfirmWorker(context, sncfReservation)
        }
        else {
            val destinationStation = maxBookerDatabase.sncfStationDao().get(sncfReservation.destination)
            displayNotification(context, sncfReservation, destinationStation)
        }
    }

    private fun displayNotification(context: Context, sncfReservation: SncfReservation, destinationStation: SncfStation) {
        registerNotificationChannel(context)

        val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.maxjeune-tgvinoui.sncf/sncf-connect/mes-voyages"))
        val contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_maxbooker_notification)
            .setContentTitle("Confirmer la réservation")
            .setContentText("Appuyez pour confirmer votre voyage à ${destinationStation.label.lowercase().replaceFirstChar{it.uppercase()}}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .setAutoCancel(false)
            .setColor(context.getColor(R.color.sncf_blue))

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(sncfReservation.orderId.hashCode(), notificationBuilder.build())
        }
    }

    private fun startAutoConfirmWorker(context: Context, sncfReservation: SncfReservation) {
        val workManager = WorkManager.getInstance(context.applicationContext)
        val travelConfirmWorkRequest = OneTimeWorkRequestBuilder<SncfApiTravelConfirmWorker>()
            .setInputData(Data.Builder()
                .putString("orderId", sncfReservation.orderId)
                .putString("origin", sncfReservation.origin)
                .putString("destination", sncfReservation.destination).build())
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED)).build()

        workManager.enqueue(travelConfirmWorkRequest)
    }
}