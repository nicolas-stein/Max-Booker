package fr.stein.maxbooker.ui.screens.settings.login.item

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.domain.model.sncf.SncfCustomer
import fr.stein.maxbooker.domain.usecase.SncfApiFetchCustomerUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsItemLoginUiState(
    var sncfCustomer: SncfCustomer? = null,
    var isSncfCustomerLoading: Boolean = false,
    var sncfCustomerLoadingError: Throwable? = null
)

@HiltViewModel
class SettingsItemLoginViewModel @Inject constructor(
    sncfCustomerDataStore: DataStore<SncfCustomerProto>,
    private val sncfAPiFetchCustomerUseCase: SncfApiFetchCustomerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsItemLoginUiState())
    val uiState: StateFlow<SettingsItemLoginUiState> = _uiState.asStateFlow()

    init {
        sncfCustomerDataStore.data.asLiveData().observeForever { sncfCustomer ->
            _uiState.update { current -> current.copy(sncfCustomer = sncfCustomer.toDomain()) }
        }

        viewModelScope.launch {
            loadSncfCustomer()
        }
    }

    suspend fun loadSncfCustomer() {
        _uiState.update { current ->
            current.copy(isSncfCustomerLoading = true, sncfCustomerLoadingError = null)
        }
        try {
            sncfAPiFetchCustomerUseCase.invoke()
        } catch (exception: SncfApiException) {
            Log.e("Max Book", "loadSncfCustomer: failed to load customer", exception)
            _uiState.update { current ->
                current.copy(
                    sncfCustomerLoadingError =
                    if (exception is SncfApiException.AuthenticatedRequired ||
                        exception is SncfApiException.NetworkException
                    ) {
                        null
                    } else {
                        exception
                    }
                )
            }
        } finally {
            _uiState.update { current -> current.copy(isSncfCustomerLoading = false) }
        }
    }
}
