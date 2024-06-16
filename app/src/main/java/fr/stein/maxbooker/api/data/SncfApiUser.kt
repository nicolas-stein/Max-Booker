package fr.stein.maxbooker.api.data

import androidx.annotation.Keep

@Keep
data class SncfApiUser(
    val firstName: String,
    val lastName: String,
    val cards: List<SncfApiCard> = emptyList()
)
