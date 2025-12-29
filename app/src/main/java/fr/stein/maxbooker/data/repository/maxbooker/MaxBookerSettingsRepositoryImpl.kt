package fr.stein.maxbooker.data.repository.maxbooker

import androidx.datastore.core.DataStore
import fr.stein.maxbooker.data.local.maxbookersettings.MaxBookerSettingsAutomationsProto
import fr.stein.maxbooker.data.local.maxbookersettings.MaxBookerSettingsProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.domain.model.maxbooker.MaxBookerSettingsAutomations
import fr.stein.maxbooker.domain.repository.sncf.MaxBookerSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class MaxBookerSettingsRepositoryImpl(dataStore: DataStore<MaxBookerSettingsProto>) : MaxBookerSettingsRepository {

    private val automationsSettingRepo = AutomationsImpl(dataStore)

    override fun automations(): MaxBookerSettingsRepository.AutomationsRepository = automationsSettingRepo

    class AutomationsImpl(private val dataStore: DataStore<MaxBookerSettingsProto>) :
        MaxBookerSettingsRepository.AutomationsRepository {

        override fun flow(): Flow<MaxBookerSettingsAutomations> = dataStore.data
            .map { it.automations.toDomain() }
            .distinctUntilChanged()

        override fun isRefreshBookingsDisabledFlow(): Flow<Boolean> =
            flow().map { it.disableRefreshBookings }.distinctUntilChanged()

        override fun isAutoConfirmBookingsDisabledFlow(): Flow<Boolean> =
            flow().map { it.disableAutoConfirmBookings }.distinctUntilChanged()

        override suspend fun setRefreshBookingsDisabled(disabled: Boolean) {
            updateAutomations { setDisableRefreshBookings(disabled) }
        }

        override suspend fun setAutoConfirmBookingsDisabled(disabled: Boolean) {
            updateAutomations { setDisableAutoConfirmBookings(disabled) }
        }

        private suspend fun updateAutomations(block: MaxBookerSettingsAutomationsProto.Builder.() -> Unit) {
            dataStore.updateData { currentData ->
                currentData.toBuilder().apply {
                    val automationsBuilder = automations.toBuilder().apply(block)
                    setAutomations(automationsBuilder)
                }.build()
            }
        }
    }
}
