package fr.stein.maxbooker.domain.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.stein.maxbooker.R
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object NotificationUtils {
    const val CHANNEL_NEW_BOOKING_ID = "MAXBOOKER_NOTIFICATION_CHANNEL_NEW_BOOKING"
    const val NOTIFICATION_NEW_BOOKING_ID = 200

    fun createNotificationChannels(context: Context) {
        val notificationChannels = listOf(
            NotificationChannel(
                CHANNEL_NEW_BOOKING_ID,
                context.getString(R.string.app_notification_new_booking_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.app_notification_new_booking_channel_description)
            }
        )

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannels(notificationChannels)
    }

    fun sendNewBookingAddedNotification(context: Context, sncfReservations: List<SncfReservation>) {
        val notificationStyle = NotificationCompat.InboxStyle()
        sncfReservations.forEach { sncfReservation ->
            notificationStyle.addLine(
                context.getString(
                    R.string.app_notification_new_booking_notification_line_text,
                    sncfReservation.destination.label,
                    sncfReservation.departureDateTime.format(
                        DateTimeFormatter.ofLocalizedDate(
                            FormatStyle.MEDIUM
                        )
                    )
                )
            )
        }

        val deepLinkUri = Uri.parse("maxbooker://bookings")
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_NEW_BOOKING_ID)
            .setSmallIcon(R.drawable.ic_maxbooker_notification)
            .setContentTitle(
                context.resources.getQuantityString(
                    R.plurals.app_notification_new_booking_notification_title,
                    sncfReservations.size,
                    sncfReservations.size
                )
            )
            .setContentText(context.getString(R.string.app_notification_new_booking_notification_content_text))
            .setStyle(notificationStyle)
            .setContentIntent(pendingIntent)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_NEW_BOOKING_ID, notification)
        }
    }
}
