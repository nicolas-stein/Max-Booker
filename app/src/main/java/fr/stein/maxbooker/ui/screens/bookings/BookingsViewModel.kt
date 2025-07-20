package fr.stein.maxbooker.ui.screens.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.stein.maxbooker.ui.domain.model.Booking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingsUiState(
    val bookings: List<Booking> = emptyList(),
    val selectedBookingId: String? = null
) {
    val selectedBooking: Booking?
        get() = bookings.find { it.orderId == selectedBookingId }
}

class BookingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BookingsUiState())
    val uiState: StateFlow<BookingsUiState> = _uiState.asStateFlow()

    init {
        fetchBookings()
    }

    private fun fetchBookings() {
        // TODO : In a real app, you'd fetch this from a repository or API
        viewModelScope.launch {
            val bookingsData = List(20) {
                Booking(
                    orderId = "booking_$it"
                )
            }
            _uiState.update { it.copy(bookings = bookingsData) }
        }
    }

    fun selectBooking(bookingId: String?) {
        _uiState.update { it.copy(selectedBookingId = bookingId) }
    }
}