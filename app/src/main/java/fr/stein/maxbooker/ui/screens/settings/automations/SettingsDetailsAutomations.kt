package fr.stein.maxbooker.ui.screens.settings.automations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.work.WorkInfo
import fr.stein.maxbooker.R
import fr.stein.maxbooker.domain.utils.UiUtils
import fr.stein.maxbooker.ui.theme.MaxBookerTheme
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailsAutomations(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: SettingsDetailsAutomationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.app_destinations_settings)) },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            RefreshBookingsListItem(
                uiState.sncfReservationsUpdateWorkInfo,
                !(uiState.maxBookerSettingsAutomations?.disableRefreshBookings ?: false),
                { viewModel.handleRefreshBookingSwitch(it) }
            )
        }
    }
}

@Composable
private fun RefreshBookingsListItem(
    sncfReservationsUpdateWorkInfo: WorkInfo?,
    switchChecked: Boolean,
    onSwitchChanged: ((Boolean) -> Unit)
) {
    ListItem(
        modifier = Modifier.clickable {
            onSwitchChanged(!switchChecked)
        },
        headlineContent = {
            Text(stringResource(R.string.screen_settings_details_automations_refresh_bookings_headline))
        },
        supportingContent = {
            Column {
                Text(stringResource(R.string.screen_settings_details_automations_refresh_bookings_description))
                if (sncfReservationsUpdateWorkInfo != null) {
                    when (sncfReservationsUpdateWorkInfo.state) {
                        WorkInfo.State.ENQUEUED -> {
                            val duration = Duration.of(
                                sncfReservationsUpdateWorkInfo.nextScheduleTimeMillis -
                                    System.currentTimeMillis(),
                                ChronoUnit.MILLIS
                            )
                            Text(
                                stringResource(
                                    R.string.screen_settings_details_automations_refresh_bookings_enqueue,
                                    UiUtils.formatDuration(duration)
                                )
                            )
                        }
                        WorkInfo.State.RUNNING -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Match the height of the CircularProgressIndicator to the Text font size
                                val textStyle = LocalTextStyle.current
                                val fontSizeInDp = with(LocalDensity.current) { textStyle.fontSize.toDp() }

                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(fontSizeInDp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(
                                        R.string.screen_settings_details_automations_refresh_bookings_running
                                    ),
                                    style = textStyle
                                )
                            }
                        }
                        WorkInfo.State.CANCELLED -> {}
                        else -> Text(sncfReservationsUpdateWorkInfo.state.toString())
                    }
                } else {
                    Text("Unknown status")
                }
            }
        },
        trailingContent = {
            Switch(
                checked = switchChecked,
                onCheckedChange = onSwitchChanged
            )
        }
    )
}

@Preview
@Composable
private fun RefreshBookingsListItemPreview() {
    MaxBookerTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            RefreshBookingsListItem(
                WorkInfo(
                    id = UUID.randomUUID(),
                    state = WorkInfo.State.ENQUEUED,
                    tags = setOf()
                ),
                switchChecked = true,
                onSwitchChanged = {}
            )
            RefreshBookingsListItem(
                WorkInfo(
                    id = UUID.randomUUID(),
                    state = WorkInfo.State.RUNNING,
                    tags = setOf()
                ),
                switchChecked = true,
                onSwitchChanged = {}
            )
        }
    }
}
