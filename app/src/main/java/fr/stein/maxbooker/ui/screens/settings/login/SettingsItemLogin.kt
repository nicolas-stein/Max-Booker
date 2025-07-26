package fr.stein.maxbooker.ui.screens.settings.login

import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import fr.stein.maxbooker.R
import fr.stein.maxbooker.ui.theme.MaxBookerTheme

@Composable
fun SettingsItemLogin(modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(stringResource(R.string.screen_settings_item_login_headline)) },
        supportingContent = { Text("Not logged in.") },
        trailingContent = {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.screen_settings_item_login_headline)
            )
        }
    )
}

@Preview
@Composable
private fun SettingsItemLoginPreview() {
    MaxBookerTheme {
        SettingsItemLogin()
    }
}
