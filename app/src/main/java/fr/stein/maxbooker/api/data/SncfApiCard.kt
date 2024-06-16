package fr.stein.maxbooker.api.data

import androidx.annotation.Keep

@Keep
data class SncfApiCard(
    val cardNumber: String,
    val productType: String
)
