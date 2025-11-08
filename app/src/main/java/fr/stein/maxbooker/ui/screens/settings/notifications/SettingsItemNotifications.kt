package fr.stein.maxbooker.ui.screens.settings.notifications

import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.stein.maxbooker.R

@Composable
fun SettingsItemNotifications(modifier: Modifier, viewModel: SettingsItemNotificationsViewModel = viewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var notificationsEnabled by remember {
        mutableStateOf(viewModel.isNotificationsEnabled(context))
    }

    // Observe lifecycle to refresh when app resumes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationsEnabled = viewModel.isNotificationsEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ListItem(
        modifier = modifier,
        headlineContent = { Text(stringResource(R.string.screen_settings_item_notifications_headline)) },
        supportingContent = {
            Text(
                stringResource(
                    if (notificationsEnabled) {
                        R.string.screen_settings_item_notifications_enabled
                    } else {
                        R.string.screen_settings_item_notifications_disabled
                    }
                )
            )
        },
        trailingContent = {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.screen_settings_item_login_headline)
            )
        }
    )
}
