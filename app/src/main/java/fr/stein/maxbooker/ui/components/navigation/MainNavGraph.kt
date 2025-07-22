package fr.stein.maxbooker.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import fr.stein.maxbooker.ui.screens.book.BookScreen
import fr.stein.maxbooker.ui.screens.bookings.BookingsScreen
import fr.stein.maxbooker.ui.screens.settings.SettingsScreen

@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = MainDestinations.BOOKINGS.route) {
        composable(
            route = "${MainDestinations.BOOKINGS.route}?bookingId={bookingId}",
            arguments = listOf(navArgument("bookingId") {
                nullable = true
                defaultValue = null
            }),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "maxbooker://bookings?bookingId={bookingId}"
                }
            )
        ) { backStachEntry ->
            val bookingId = backStachEntry.arguments?.getString("bookingId")
            BookingsScreen(initialBookingId = bookingId)
        }
        composable(MainDestinations.BOOK.route) {
            BookScreen()
        }
        composable (
            route = "${MainDestinations.SETTINGS.route}?itemName={itemName}",
            arguments = listOf(navArgument("itemName") {
                nullable = true
                defaultValue = null
            }),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "maxbooker://settings?itemName={itemName}"
                }
            )
        ) { backStachEntry ->
            val itemName = backStachEntry.arguments?.getString("itemName")
            SettingsScreen(initialItemName = itemName)
        }
    }
}