package fr.stein.maxbooker.database.reservations

data class SncfSeat(
    val facingForward: Boolean,
    val seatPosition: String,
    val spaceType: String,
    val tgvDeck: String
) {
    fun seatPositionToStr(): String {
        return assignmentToStr(seatPosition)
    }

    fun spaceTypeToStr(): String {
        return assignmentToStr(spaceType)
    }

    fun tgvDeckToStr(): String {
        return assignmentToStr(tgvDeck)
    }

    private fun assignmentToStr(assignment: String): String {
        return when(assignment) {
            "AACC" -> "Espace accompagnateur handicapé"
            "ACAL" -> "Calme"
            "ACAR" -> "Carré"
            "ACDO" -> "Club duo vis-à-vis"
            "ACEN" -> "Carré enfants"
            "ACMP" -> "Compartiment"
            "ACOU" -> "Couloir"
            "ACQT" -> "Club quatre"
            "ADOC" -> "Duo côte à côte"
            "AEEC" -> "Espace enfant"
            "AEQT" -> "Club six"
            "AFEN" -> "Fenêtre"
            "AFIS" -> "Isolée"
            "AHAN" -> "Espace handicapé"
            "AHDC" -> "Place handicapé"
            "AINC" -> "Assise - Inclinable"
            "AKID" -> "Espace KID"
            "AKSK" -> "Kiosque"
            "AMIL" -> "Assise - Milieu"
            "AMOM" -> "Espace Famille"
            "ANBI" -> "Niveau bas impératif - Duplex"
            "ANHI" -> "Niveau haut impératif - Duplex"
            "ANUR" -> "Family Eurostar Nursery"
            "ANVB" -> "Salle Basse"
            "ANVH" -> "Salle Haute"
            "APAN" -> "Panorama"
            "ASDM" -> "Sens de la marche"
            "ASII" -> "Silence impératif"
            "ASIL" -> "Silence"
            "ASLL" -> "Salle"
            "ASLO" -> "Solo"
            "ATAB" -> "Siège avec table"
            "ATEL" -> "Téléphone"
            "CBAS" -> "Bas"
            "CDAS" -> "Dame seule"
            "CHAU" -> "Haut"
            "CIRE" -> "Couchette - Bas impératif"
            "CMIL" -> "Milieu"
            "LBAS" -> "Lit - Bas"
            "LHAU" -> "Lit - Haut"
            "LMIL" -> "Lit - Milieu"
            "VCFA" -> "Compartiment - Famille"
            "VCFE" -> "Compartiment - Femme"
            "VCHO" -> "Compartiment - Homme"
            "CPRI" -> "Couchette Espace Privatif"
            "CFAM" -> "Couchette Espace Privatif Famille"
            "AVEP" -> "Assise avec Vélo payant"
            "CVEP" -> "Couchette avec Vélo payant"
            "LVEP" -> "Lit avec Vélo payant"
            "AVGL" -> "Place avec chien guide"
            "APAF" -> "Place accès Facile"
            "ATEI" -> "Téléphone impératif"
            "AVEG" -> "Assise avec Vélo gratuit"
            "AFAM" -> "Famille"
            else -> ""
        }
    }
}