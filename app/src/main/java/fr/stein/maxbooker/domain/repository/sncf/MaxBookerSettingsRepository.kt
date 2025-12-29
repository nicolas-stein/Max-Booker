package fr.stein.maxbooker.domain.repository.sncf

import fr.stein.maxbooker.domain.model.maxbooker.MaxBookerSettingsAutomations
import kotlinx.coroutines.flow.Flow

interface MaxBookerSettingsRepository {
    interface AutomationsRepository {
        fun flow(): Flow<MaxBookerSettingsAutomations>
        fun isRefreshBookingsDisabledFlow(): Flow<Boolean>
        suspend fun setRefreshBookingsDisabled(disabled: Boolean)

        fun isAutoConfirmBookingsDisabledFlow(): Flow<Boolean>
        suspend fun setAutoConfirmBookingsDisabled(disabled: Boolean)
    }

    fun automations(): AutomationsRepository
}
