package fr.stein.maxbooker.domain.model

data class SncfApiAuthentication(val sncfApiToken: SncfApiToken, val cookies: String)

data class SncfApiToken(
    val expiresIn: Int,
    val idToken: String,
    val refreshToken: String,
    val tokenType: String
)
