package fr.stein.maxbooker.data.mapper

import fr.stein.maxbooker.data.local.maxbookersettings.MaxBookerSettingsAutomationsProto
import fr.stein.maxbooker.data.local.maxbookersettings.MaxBookerSettingsProto
import fr.stein.maxbooker.domain.model.maxbooker.MaxBookerSettings
import fr.stein.maxbooker.domain.model.maxbooker.MaxBookerSettingsAutomations

fun MaxBookerSettingsProto.toDomain(): MaxBookerSettings = MaxBookerSettings(
    automations.toDomain()
)

fun MaxBookerSettingsAutomationsProto.toDomain(): MaxBookerSettingsAutomations = MaxBookerSettingsAutomations(
    disableRefreshBookings = disableRefreshBookings,
    disableAutoConfirmBookings = disableAutoConfirmBookings
)
