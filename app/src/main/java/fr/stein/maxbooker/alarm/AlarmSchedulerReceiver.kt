package fr.stein.maxbooker.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import fr.stein.maxbooker.database.MaxBookerDatabase
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmSchedulerReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) = goAsync {
        Log.i("Max Book", "AlarmSchedulerReceiver triggered !")
        if (context == null || intent == null || intent.action == null) {
            Log.e("Max Book", "AlarmSchedulerReceiver cannot schedule alarm !")
            return@goAsync
        }

        if (!listOf("android.intent.action.BOOT_COMPLETED",
                "android.intent.action.QUICKBOOT_POWERON",
                "android.intent.action.REBOOT",
                "fr.stein.maxbooker.SCHEDULE_ALARMS").contains(intent.action)) {
            Log.e("Max Book", "AlarmSchedulerReceiver cannot schedule alarm: unknown action ${intent.action} !")
            return@goAsync
        }

        val sncfReservations = MaxBookerDatabase.getInstance(context)
            .sncfReservationDao().getAllLaterThanDateTime(LocalDateTime.now().minusHours(48))
            .filter { it.travelConfirmed != "CONFIRMED" && it.travelConfirmed != "TOO_LATE_TO_CONFIRM" && it.travelConfirmed != "CANCELED"}
        Log.i("Max Book", "AlarmSchedulerReceiver: ${sncfReservations.count()} notifications to generate !")

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        for (sncfReservation in sncfReservations){
            val alarmIntent = Intent(context, TravelConfirmAlarmReceiver::class.java).apply {
                putExtra("orderId", sncfReservation.orderId)
                putExtra("origin", sncfReservation.origin)
                putExtra("destination", sncfReservation.destination)
            }

            if (sncfReservation.departureDateTime.minusHours(48).isAfter(LocalDateTime.now())) {
                val alarmTime = sncfReservation.departureDateTime.minusHours(48).atZone(ZoneId.systemDefault()).toEpochSecond()*1000L
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, alarmTime, PendingIntent.getBroadcast(
                    context,
                    sncfReservation.orderId.hashCode(),
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                ))
                Log.i("Max Book", "Scheduled alarm at ${sncfReservation.departureDateTime.minusHours(48).atZone(ZoneId.systemDefault())}")
            }
            else {
                context.sendBroadcast(alarmIntent)
                Log.i("Max Book", "Triggered alarm intent right now !")
            }
        }
    }
}