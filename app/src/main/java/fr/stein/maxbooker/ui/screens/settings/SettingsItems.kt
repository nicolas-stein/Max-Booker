package fr.stein.maxbooker.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.stein.maxbooker.ui.screens.settings.login.SettingsDetailsLogin
import fr.stein.maxbooker.ui.screens.settings.login.SettingsItemLogin

enum class SettingsItems() {
    LOGIN {
        @Composable
        override fun ListComposable(modifier: Modifier) {
            SettingsItemLogin(modifier)
        }

        @Composable
        override fun DetailsComposable(
            modifier: Modifier,
            navigateBack: () -> Unit
        ) {
            SettingsDetailsLogin(modifier, navigateBack)
        }
    };

    @Composable abstract fun ListComposable(modifier: Modifier)
    @Composable abstract fun DetailsComposable(modifier: Modifier, navigateBack: () -> Unit)
}