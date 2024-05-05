package fr.stein.maxbooker.api.data

data class SncfApiToken (
    val expiresIn: Int,
    val idToken: String,
    val refreshToken: String,
    val tokenType: String
)