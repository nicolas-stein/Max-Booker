package fr.stein.maxbooker.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.stein.maxbooker.ui.screens.settings.login.details.SettingsDetailsLogin
import fr.stein.maxbooker.ui.screens.settings.login.item.SettingsItemLogin
import fr.stein.maxbooker.ui.screens.settings.notifications.SettingsDetailsNotifications
import fr.stein.maxbooker.ui.screens.settings.notifications.SettingsItemNotifications

enum class SettingsItems {
    LOGIN {
        @Composable
        override fun ListComposable(modifier: Modifier) {
            SettingsItemLogin(modifier)
        }

        @Composable
        override fun DetailsComposable(modifier: Modifier, navigateBack: () -> Unit) {
            SettingsDetailsLogin(modifier, navigateBack)
        }
    },
    NOTIFICATIONS {
        @Composable
        override fun ListComposable(modifier: Modifier) {
            SettingsItemNotifications(modifier)
        }

        @Composable
        override fun DetailsComposable(modifier: Modifier, navigateBack: () -> Unit) {
            SettingsDetailsNotifications(modifier, navigateBack)
        }
    };

    @Composable abstract fun ListComposable(modifier: Modifier)

    @Composable abstract fun DetailsComposable(modifier: Modifier, navigateBack: () -> Unit)
}
