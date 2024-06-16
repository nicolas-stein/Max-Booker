package fr.stein.maxbooker.api.data

import androidx.annotation.Keep

@Keep
data class SncfApiToken (
    val expiresIn: Int,
    val idToken: String,
    val refreshToken: String,
    val tokenType: String
)