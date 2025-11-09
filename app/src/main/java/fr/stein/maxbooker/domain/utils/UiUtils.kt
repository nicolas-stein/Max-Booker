package fr.stein.maxbooker.domain.utils

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

object UiUtils {
    fun formatTimeWithLeadingZeros(dateTime: TemporalAccessor): String {
        // Check if locale uses 12-hour format
        val shortTimePattern = (
            SimpleDateFormat.getTimeInstance(
                SimpleDateFormat.SHORT
            ) as SimpleDateFormat
            ).toPattern()
        val usesAmPm = shortTimePattern.contains("a")

        val formatterPattern = if (usesAmPm) "hh:mm a" else "HH:mm"

        val formatter = DateTimeFormatter.ofPattern(formatterPattern)
        return formatter.format(dateTime)
    }

    fun formatDuration(duration: Duration): String {
        var result = ""
        if (duration.toHours() > 0) {
            result += "${duration.toHours()}h"
        }
        result += "${duration.minusHours(duration.toHours()).toMinutes()}min"

        return result
    }
}
