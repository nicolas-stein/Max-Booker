package fr.stein.maxbooker.domain.fetcher

sealed class DataState<out T> {
    data object Loading : DataState<Nothing>()
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val exception: Exception) : DataState<Nothing>()
    data class Offline<out T>(val data: T?): DataState<T>()
}