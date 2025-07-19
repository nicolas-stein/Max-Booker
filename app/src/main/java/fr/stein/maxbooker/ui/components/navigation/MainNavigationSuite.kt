package fr.stein.maxbooker.ui.components.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

@Composable
fun MainNavigationSuite(
    navController: NavHostController,
    currentDestination: NavDestination?,
    content: @Composable () -> Unit
) {
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            MainDestinations.entries.forEach { item ->
                val isItemSelected = currentDestination?.hierarchy?.any { destination -> destination.route?.substringBefore("?") == item.route } == true
                item(
                    selected = isItemSelected,
                    icon = { Icon(ImageVector.vectorResource(if(isItemSelected) item.iconSelected else item.iconUnselected), contentDescription = item.name) },
                    label = { Text(stringResource(item.label)) },
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
        content = { content() }
    )
}