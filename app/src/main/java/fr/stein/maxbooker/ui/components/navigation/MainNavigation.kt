package fr.stein.maxbooker.ui.components.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavigation(initialDeepLinkUri: Uri? = null) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val deepLinkUri by rememberUpdatedState(initialDeepLinkUri)

    LaunchedEffect(deepLinkUri) {
        deepLinkUri?.let { uri ->
            if (uri.scheme == "maxbooker" && uri.host?.uppercase() == MainDestinations.BOOKINGS.route) {
                val queryParameters = uri.queryParameterNames
                    .mapNotNull { name ->
                        uri.getQueryParameter(name)?.let { value ->
                            "$name=$value"
                        }
                    }
                    .joinToString("&")

                val route = if (queryParameters.isNotEmpty()) {
                    "${MainDestinations.BOOKINGS.route}?$queryParameters"
                } else {
                    MainDestinations.BOOKINGS.route
                }

                navController.popBackStack(navController.graph.id, true)

                navController.navigate(route) {
                    launchSingleTop = true
                }
            }
        }
    }

    MainNavigationSuite(
        navController = navController,
        currentDestination = currentDestination
        ) {
        MainNavGraph(navController = navController)
    }
}