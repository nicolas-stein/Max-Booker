package fr.stein.maxbooker.ui.screens.settings.login.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.stein.maxbooker.domain.fetcher.DataState
import fr.stein.maxbooker.domain.fetcher.sncf.SncfApiCustomerFetcher
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SettingsItemLoginUiState(val sncfCustomerData: DataState<SncfCustomer> = DataState.Offline(null))

@HiltViewModel
class SettingsItemLoginViewModel @Inject constructor(sncfCustomerFetcher: SncfApiCustomerFetcher) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsItemLoginUiState())
    val uiState: StateFlow<SettingsItemLoginUiState> = _uiState.asStateFlow()

    init {
        sncfCustomerFetcher.customerState.onEach { dataState ->
            _uiState.update { currentState ->
                currentState.copy(sncfCustomerData = dataState)
            }
        }.launchIn(viewModelScope)
    }
}
