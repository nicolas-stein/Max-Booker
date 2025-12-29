package fr.stein.maxbooker.data.repository.maxbooker

import androidx.datastore.core.DataStore
import fr.stein.maxbooker.data.local.maxbookersettings.MaxBookerSettingsProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.domain.model.maxbooker.MaxBookerSettingsAutomations
import fr.stein.maxbooker.domain.repository.sncf.MaxBookerSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaxBookerSettingsRepositoryImpl(maxBookerSettingsDataStore: DataStore<MaxBookerSettingsProto>) :
    MaxBookerSettingsRepository {
    class AutomationsImpl(private val maxBookerSettingsDataStore: DataStore<MaxBookerSettingsProto>) :
        MaxBookerSettingsRepository.AutomationsRepository {
        override fun flow(): Flow<MaxBookerSettingsAutomations> = maxBookerSettingsDataStore.data.map {
            it.automations.toDomain()
        }

        override fun isRefreshBookingsDisabledFlow(): Flow<Boolean> = flow().map { it.disableRefreshBookings }
        override suspend fun setRefreshBookingsDisabled(disabled: Boolean) {
            maxBookerSettingsDataStore.updateData { currentData ->
                currentData.toBuilder()
                    .setAutomations(
                        currentData.automations.toBuilder()
                            .setDisableRefreshBookings(disabled)
                            .build()
                    )
                    .build()
            }
        }
    }

    val automationsImpl = AutomationsImpl(maxBookerSettingsDataStore)
    override fun automations(): MaxBookerSettingsRepository.AutomationsRepository = automationsImpl
}
