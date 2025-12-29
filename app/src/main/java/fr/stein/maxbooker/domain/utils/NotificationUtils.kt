package fr.stein.maxbooker.domain.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import fr.stein.maxbooker.R
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object NotificationUtils {
    const val CHANNEL_NEW_BOOKING_ID = "MAXBOOKER_NOTIFICATION_CHANNEL_NEW_BOOKING"
    const val CHANNEL_BOOKING_CONFIRMED_ID = "MAXBOOKER_NOTIFICATION_CHANNEL_BOOKING_CONFIRMED"

    const val NOTIFICATION_NEW_BOOKING_ID = 200
    const val NOTIFICATION_BOOKING_CONFIRMED_ID = 201

    fun createNotificationChannels(context: Context) {
        val notificationChannels = listOf(
            NotificationChannel(
                CHANNEL_NEW_BOOKING_ID,
                context.getString(R.string.app_notification_new_booking_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.app_notification_new_booking_channel_description)
            },
            NotificationChannel(
                CHANNEL_BOOKING_CONFIRMED_ID,
                context.getString(R.string.app_notification_booking_confirmed_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.app_notification_booking_confirmed_channel_description)
            }
        )

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannels(notificationChannels)
    }

    fun sendNewBookingAddedNotification(context: Context, sncfReservations: List<SncfReservation>) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("Max Book", "sendNewBookingAddedNotification: cannot send notification, missing permissions !")
            return
        }

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

        val deepLinkUri = "maxbooker://bookings".toUri()
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
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_NEW_BOOKING_ID, notification)
        }
    }

    fun sendBookingConfirmedNotification(context: Context, sncfReservation: SncfReservation) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("Max Book", "sendBookingConfirmedNotification: cannot send notification, missing permissions !")
            return
        }

        val deepLinkUri = "maxbooker://bookings?orderId=${sncfReservation.orderId}".toUri()
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BOOKING_CONFIRMED_ID)
            .setSmallIcon(R.drawable.ic_maxbooker_notification)
            .setContentTitle(
                context.getString(R.string.app_notification_booking_confirmed_notification_title)
            )
            .setContentText(
                context.getString(
                    R.string.app_notification_booking_confirmed_notification_content_text,
                    sncfReservation.destination.label,
                    sncfReservation.departureDateTime.format(
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    )
                )
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        context.getString(
                            R.string.app_notification_booking_confirmed_notification_big_text,
                            sncfReservation.destination.label,
                            sncfReservation.departureDateTime.format(
                                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                            )
                        )
                    )
            )
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setColor(Color.GREEN)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_BOOKING_CONFIRMED_ID, notification)
        }
    }

    fun sendBookingConfirmedFailedNotification(context: Context, sncfReservation: SncfReservation) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("Max Book", "sendBookingConfirmedFailedNotification: cannot send notification, missing permissions !")
            return
        }

        val deepLinkUri = "maxbooker://bookings?orderId=${sncfReservation.orderId}".toUri()
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BOOKING_CONFIRMED_ID)
            .setSmallIcon(R.drawable.ic_maxbooker_notification)
            .setContentTitle(
                context.getString(R.string.app_notification_booking_confirmed_failed_notification_title)
            )
            .setContentText(
                context.getString(
                    R.string.app_notification_booking_confirmed_failed_notification_content_text,
                    sncfReservation.destination.label,
                    sncfReservation.departureDateTime.format(
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    )
                )
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        context.getString(
                            R.string.app_notification_booking_confirmed_failed_notification_big_text,
                            sncfReservation.destination.label,
                            sncfReservation.departureDateTime.format(
                                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                            )
                        )
                    )
            )
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setColor(Color.RED)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_BOOKING_CONFIRMED_ID, notification)
        }
    }
}
