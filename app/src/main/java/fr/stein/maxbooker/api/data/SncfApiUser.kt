package fr.stein.maxbooker.api.data

data class SncfApiUser(
    val firstName: String,
    val lastName: String,
    val cards: List<SncfApiCard> = emptyList()
)
