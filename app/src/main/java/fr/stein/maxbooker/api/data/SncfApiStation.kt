package fr.stein.maxbooker.api.data

import fr.stein.maxbooker.database.reservations.SncfStation

data class SncfApiStation(
    val label: String,
    val rrCode: String
) {
    fun toSncfStation(): SncfStation {
        return SncfStation(rrcode = rrCode, label=label)
    }
}
