package com.mKanta.archivemaps.data.repository

import com.mKanta.archivemaps.data.local.UserPreferences
import javax.inject.Inject

class UserPreferencesRepository
    @Inject
    constructor(
        private val userPreferences: UserPreferences,
    ) {
        val showListIntroFlow = userPreferences.showListIntroFlow

        suspend fun setShowListIntro(value: Boolean) {
            userPreferences.saveShowListIntro(value)
        }
    }
