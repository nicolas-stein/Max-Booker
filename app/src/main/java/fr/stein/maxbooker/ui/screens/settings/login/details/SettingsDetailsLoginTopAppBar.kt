package fr.stein.maxbooker.ui.screens.settings.login.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fr.stein.maxbooker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailsLoginTopAppBar(
    onBackClick: () -> Unit,
    onClearCookieClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(),
        title = { Text(stringResource(R.string.screen_settings_item_login_headline)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.screen_settings_login_topbar_clear_cookies))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(
                                R.string.screen_settings_login_topbar_clear_cookies
                            )
                        )
                    },
                    onClick = {
                        showMenu = false
                        onClearCookieClick()
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.screen_settings_login_topbar_restart)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(
                                R.string.screen_settings_login_topbar_restart
                            )
                        )
                    },
                    onClick = {
                        showMenu = false
                        onRestartClick()
                    }
                )
            }
        }
    )
}
