package fr.stein.maxbooker.api.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import fr.stein.maxbooker.R

class SncfApiWorkerNotifications {
    companion object {
        val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_WORKERS"
        val NOTIFICATION_ID_SNCF_API_AUTH_TOKEN_WORKER = 1
        val NOTIFICATION_ID_SNCF_API_USER_WORKER = 2
        val NOTIFICATION_ID_SNCF_API_RESERVATION_WORKER = 3
        val NOTIFICATION_ID_SNCF_API_TRAVEL_DETAILS_WORKER = 4
        val NOTIFICATION_ID_SNCF_API_TRAVEL_CONFIRM_WORKER = 5

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Synchronisation", NotificationManager.IMPORTANCE_LOW)

                channel.description = "Notifications liés aux services de synchronisation"
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            }
        }

        private fun baseNotification(context: Context, notificationText: String): Notification {
            return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_maxbooker_notification)
                .setContentTitle("Synchronisation")
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(0, 1, true)
                .setOngoing(true)
                .build()
        }

        fun getAuthTokenWorkerNotification(context: Context): Notification {
            createNotificationChannel(context)
            return baseNotification(context, "Récupération du jeton d'authentification...")
        }

        fun getUserWorkerNotification(context: Context): Notification {
            createNotificationChannel(context)
            return baseNotification(context, "Récupération des données utilisateur...")
        }

        fun getReservationWorkerNotification(context: Context): Notification {
            createNotificationChannel(context)
            return baseNotification(context, "Récupération des réservations...")
        }

        fun getTravelDetailsWorkerNotification(context: Context): Notification {
            createNotificationChannel(context)
            return baseNotification(context, "Récupération des détails du trajet...")
        }

        fun getTravelConfirmWorkerNotification(context: Context): Notification {
            createNotificationChannel(context)
            return baseNotification(context, "Confirmation de la réservation...")
        }
    }
}