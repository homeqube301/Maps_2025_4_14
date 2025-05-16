package com.mKanta.archivemaps.data.repository

import com.mKanta.archivemaps.data.local.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserPreferencesRepository {
    val showListIntroFlow: Flow<Boolean>
    val showMapIntroFlow: Flow<Boolean>
    val showDetailIntroFlow: Flow<Boolean>

    suspend fun setShowListIntro(value: Boolean)

    suspend fun setShowMapIntro(value: Boolean)

    suspend fun setShowDetailIntro(value: Boolean)
}

class UserPreferencesRepositoryImpl
    @Inject
    constructor(
        private val userPreferences: UserPreferences,
    ) : UserPreferencesRepository {
        override val showListIntroFlow = userPreferences.showListIntroFlow
        override val showMapIntroFlow = userPreferences.showMapIntroFlow
        override val showDetailIntroFlow = userPreferences.showDetailIntroFlow

        override suspend fun setShowListIntro(value: Boolean) {
            userPreferences.saveShowListIntro(value)
        }

        override suspend fun setShowMapIntro(value: Boolean) {
            userPreferences.saveShowMapIntro(value)
        }

        override suspend fun setShowDetailIntro(value: Boolean) {
            userPreferences.saveShowDetailIntro(value)
        }
    }