package fr.stein.maxbooker.data.remote

data class SncfApiTokenDto(
    val expiresIn: Int,
    val idToken: String,
    val refreshToken: String,
    val tokenType: String
)
