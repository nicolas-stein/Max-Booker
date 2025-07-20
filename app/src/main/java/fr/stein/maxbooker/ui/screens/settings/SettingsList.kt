package fr.stein.maxbooker.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fr.stein.maxbooker.R
import fr.stein.maxbooker.ui.theme.MaxBookerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsList(
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberLazyListState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.app_destinations_settings)) },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
            state = scrollState,
            modifier = modifier.padding(innerPadding)
        ) {
            items(SettingsItems.entries) { settingsItem ->
                settingsItem.ListComposable(
                    modifier = modifier.clickable{ onItemClick(settingsItem.name) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsListPreview() {
    MaxBookerTheme {
        SettingsList(onItemClick = {})
    }
}