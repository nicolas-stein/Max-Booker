package fr.stein.maxbooker.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import fr.stein.maxbooker.domain.usecase.SncfApiAuthenticateUseCase

@EntryPoint
@InstallIn(ActivityComponent::class)
interface SncfApiUseCasesEntryPoint {
    fun getSncfApiAuthenticateUseCase(): SncfApiAuthenticateUseCase
}