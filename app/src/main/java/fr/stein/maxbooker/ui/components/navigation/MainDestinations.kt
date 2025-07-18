package fr.stein.maxbooker.ui.components.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import fr.stein.maxbooker.R

enum class MainDestinations(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val iconSelected: Int,
    @DrawableRes val iconUnselected: Int,
) {
    BOOKINGS("BOOKINGS",
        R.string.app_destinations_bookings,
        R.drawable.ic_bookings_tab_selected,
        R.drawable.ic_bookings_tab_unselected),
    BOOK("BOOK",
        R.string.app_destinations_book,
        R.drawable.ic_bookings_tab_selected,
        R.drawable.ic_bookings_tab_unselected),
    SETTINGS("SETTINGS",
        R.string.app_destinations_settings,
        R.drawable.ic_settings_filled,
        R.drawable.ic_settings_unfilled),
}