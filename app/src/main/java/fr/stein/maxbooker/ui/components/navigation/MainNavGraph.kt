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
            route = "${MainDestinations.BOOKINGS.route}?dvNumber={dvNumber}",
            arguments = listOf(
                navArgument("dvNumber") {
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "maxbooker://bookings?dvNumber={dvNumber}"
                }
            )
        ) { backStachEntry ->
            val dvNumber = backStachEntry.arguments?.getString("dvNumber")
            BookingsScreen(initialDvNumber = dvNumber)
        }
        composable(MainDestinations.BOOK.route) {
            BookScreen()
        }
        composable(
            route = "${MainDestinations.SETTINGS.route}?itemName={itemName}",
            arguments = listOf(
                navArgument("itemName") {
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "maxbooker://settings?itemName={itemName}"
                }
            )
        ) { backStachEntry ->
            val itemName = backStachEntry.arguments?.getString("itemName")
            SettingsScreen(initialItemName = itemName?.uppercase())
        }
    }
}
