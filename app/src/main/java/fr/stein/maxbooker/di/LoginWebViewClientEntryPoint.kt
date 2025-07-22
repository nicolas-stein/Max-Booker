package fr.stein.maxbooker.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import fr.stein.maxbooker.domain.repository.SncfApiRepository

@EntryPoint
@InstallIn(ActivityComponent::class)
interface LoginWebViewClientEntryPoint {
    fun getSncfApiRepository(): SncfApiRepository
}