package fr.stein.maxbooker.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.stein.maxbooker.R
import fr.stein.maxbooker.ui.theme.MaxBookerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    initialItemName: String? = null,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.selectedItemName) {
        val selectedItemName = uiState.selectedItemName
        if (selectedItemName != null) {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail, selectedItemName)
        } else {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.List, selectedItemName)
        }
    }

    LaunchedEffect(listDetailNavigator.currentDestination) {
        if (listDetailNavigator.currentDestination?.pane == ListDetailPaneScaffoldRole.List) {
            viewModel.selectItem(null)
        }
    }

    LaunchedEffect(initialItemName) {
        if (initialItemName != null) {
            viewModel.selectItem(initialItemName)
        }
    }

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = {
            SettingsList(
                onItemClick = { itemName -> viewModel.selectItem(itemName) }
            )
        },
        detailPane = {
            val selectedItem = SettingsItems.entries.firstOrNull {
                it.name ==
                    uiState.selectedItemName
            }
            if (selectedItem != null) {
                selectedItem.DetailsComposable(modifier, {
                    scope.launch {
                        listDetailNavigator.navigateBack()
                    }
                    viewModel.selectItem(null)
                })
            } else {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.screen_settings_none_selected))
                }
            }
        }
    )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    MaxBookerTheme {
        SettingsScreen()
    }
}
