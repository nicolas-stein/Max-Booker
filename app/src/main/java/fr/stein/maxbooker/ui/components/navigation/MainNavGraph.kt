package fr.stein.maxbooker.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.stein.maxbooker.ui.screens.book.BookScreen
import fr.stein.maxbooker.ui.screens.bookings.BookingsScreen
import fr.stein.maxbooker.ui.screens.settings.SettingsScreen

@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = MainDestinations.BOOKINGS.route) {
        composable(MainDestinations.BOOKINGS.route) {
            BookingsScreen()
        }
        composable(MainDestinations.BOOK.route) {
            BookScreen()
        }
        composable (MainDestinations.SETTINGS.route) {
            SettingsScreen()
        }
    }
}