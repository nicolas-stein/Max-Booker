package fr.stein.maxbooker.ui.tab.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.stein.maxbooker.proto.AppSettings
import fr.stein.maxbooker.ui.common.DurationPickerDialog
import fr.stein.maxbooker.ui.common.formatDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration

@Composable
@Suppress("Since15")
fun SettingsTab(settingsTabViewModel: SettingsTabViewModel= viewModel(),
                fragmentManager: FragmentManager? = null,
                appSettingsDatastore: DataStore<AppSettings>? = null) {
    val settingsUiState by settingsTabViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        Text(text = "Mes réservations",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp))
        SwitchWithLabel(label = "Réservations passées",
            sublabel = "Afficher les réservations passées de ${formatDuration(settingsUiState.pastReservationsDuration)}",
            state = settingsUiState.showPastReservations,
            onStateChange = { state ->
                settingsTabViewModel.showPastReservationsSwitchStateChanged(state)
                coroutineScope.launch(Dispatchers.IO) {
                    appSettingsDatastore?.updateData { currentData ->
                        val myReservationsBuilder = currentData.myReservationsSettings.toBuilder()
                        myReservationsBuilder.showPastReservations = state
                        currentData.toBuilder().setMyReservationsSettings(myReservationsBuilder.build()).build()
                    }
                }
            },
            onLabelClicked = {
                DurationPickerDialog(
                    showSecondsPicker = false,
                    showMinutesPicker = false,
                    showHoursPicker = false,
                    onAccept = { dialog ->
                        settingsTabViewModel.pastReservationsDurationChanged(dialog.getSelectedDuration())
                        settingsTabViewModel.showPastReservationsSwitchStateChanged(true)
                        coroutineScope.launch(Dispatchers.IO) {
                            appSettingsDatastore?.updateData { currentData ->
                                val myReservationsBuilder = currentData.myReservationsSettings.toBuilder()
                                myReservationsBuilder.pastReservationsDuration = dialog.getSelectedDuration().toSeconds()
                                myReservationsBuilder.showPastReservations = true
                                currentData.toBuilder().setMyReservationsSettings(myReservationsBuilder.build()).build()
                            }
                        }
                    })
                    .show(fragmentManager!!, "DURATION_PICKER")
            })
        SwitchWithLabel(label = "Confirmation automatique",
            sublabel = "Confirme automatiquement 48H avant le départ",
            state = settingsUiState.autoConfirmReservations,
            onStateChange = { state ->
                settingsTabViewModel.autoConfirmReservationsSwitchStateChanged(state)
                coroutineScope.launch(Dispatchers.IO) {
                    appSettingsDatastore?.updateData { currentData ->
                        val myReservationsBuilder = currentData.myReservationsSettings.toBuilder()
                        myReservationsBuilder.autoConfirmReservations = state
                        currentData.toBuilder().setMyReservationsSettings(myReservationsBuilder.build()).build()
                    }
                }
            })
    }

    LaunchedEffect(true) {
        if (appSettingsDatastore == null) {
            return@LaunchedEffect
        }
        val appSettings = appSettingsDatastore.data.first()
        val myReservationsSettings = appSettings.myReservationsSettings
        settingsTabViewModel.showPastReservationsSwitchStateChanged(myReservationsSettings.showPastReservations)
        val appSettingsPastReservationsDuration = myReservationsSettings.pastReservationsDuration
        if (appSettingsPastReservationsDuration == 0L) {
            appSettingsDatastore.updateData { currentData -> currentData.toBuilder()
                .setMyReservationsSettings(currentData.myReservationsSettings.toBuilder()
                    .setPastReservationsDuration(settingsUiState.pastReservationsDuration.toSeconds())
                    .build()).build() }
        }
        else {
            settingsTabViewModel.pastReservationsDurationChanged(Duration.ofSeconds(appSettingsPastReservationsDuration))
        }
        settingsTabViewModel.autoConfirmReservationsSwitchStateChanged(myReservationsSettings.autoConfirmReservations)
    }
}

@Preview
@Composable
private fun SettingsTabPreview() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(Color.White)) {
        SettingsTab()
    }
}

@Composable
private fun SwitchWithLabel(label: String,
                            sublabel: String? = null,
                            state: Boolean,
                            onStateChange: (Boolean) -> Unit,
                            onLabelClicked: (() -> Unit)? = null) {

    val interactionSource = remember { MutableInteractionSource() }
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                if (onLabelClicked != null) {
                    onLabelClicked()
                } else {
                    onStateChange(!state)
                }
            }
        )) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(16.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = label, style = MaterialTheme.typography.labelLarge)
                if (sublabel != null) {
                    Text(text = sublabel, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .weight(1F))

            if (onLabelClicked != null) {
                VerticalDivider(modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp, 0.dp))
            }

            Switch(checked = state, onCheckedChange = { onStateChange(it) })}
    }
}