package fr.stein.maxbooker.ui.common

import java.time.Duration

@Suppress("Since15")
fun formatDuration(duration: Duration): String {
    val days = duration.toDaysPart()
    val hours = duration.toHoursPart()
    val minutes = duration.toMinutesPart()
    val seconds = duration.toSecondsPart()

    val strings = mutableListOf<String>()
    if (days > 0) {
        strings.add("$days jours")
    }
    if (hours > 0) {
        strings.add("$hours heures")
    }
    if (minutes > 0) {
        strings.add("$minutes minutes")
    }
    if (seconds > 0) {
        strings.add("$seconds secondes")
    }

    return strings.joinToString(" ")
}