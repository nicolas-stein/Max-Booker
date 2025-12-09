package fr.stein.maxbooker.ui.screens.settings.automations

import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import fr.stein.maxbooker.R

@Composable
fun SettingsItemAutomations(modifier: Modifier) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(stringResource(R.string.screen_settings_item_automations_headline)) },
        supportingContent = {
            Text(stringResource(R.string.screen_settings_item_automations_description))
        },
        trailingContent = {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.screen_settings_item_login_headline)
            )
        }
    )
}
