package com.mKanta.archivemaps.data.repository

import com.mKanta.archivemaps.data.local.UserPreferences
import javax.inject.Inject

class UserPreferencesRepository
    @Inject
    constructor(
        private val userPreferences: UserPreferences,
    ) {
        val showListIntroFlow = userPreferences.showListIntroFlow
        val showMapIntroFlow = userPreferences.showMapIntroFlow
        val showDetailIntroFlow = userPreferences.showDetailIntroFlow

        suspend fun setShowListIntro(value: Boolean) {
            userPreferences.saveShowListIntro(value)
        }

        suspend fun setShowMapIntro(value: Boolean) {
            userPreferences.saveShowMapIntro(value)
        }

        suspend fun setShowDetailIntro(value: Boolean) {
            userPreferences.saveShowDetailIntro(value)
    }
    }
