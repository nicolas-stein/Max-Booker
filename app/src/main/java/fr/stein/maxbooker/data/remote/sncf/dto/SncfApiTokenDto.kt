package fr.stein.maxbooker.data.remote.sncf.dto

data class SncfApiTokenDto(
    val expiresIn: Int,
    val idToken: String,
    val refreshToken: String,
    val tokenType: String
)
