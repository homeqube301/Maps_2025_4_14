package com.mKanta.archivemaps.domain.repository

import com.mKanta.archivemaps.domain.model.GuestUser
import com.mKanta.archivemaps.domain.model.User

interface AuthRepository {
    suspend fun signUp(
        email: String,
        password: String,
    ): Result<Int>

    suspend fun signIn(
        email: String,
        password: String,
    ): Result<Int>

    suspend fun signOut(): Result<Unit>

    suspend fun getCurrentUserId(): String?

    suspend fun isAuthenticated(): Boolean

    suspend fun getCurrentUser(): User?

    suspend fun updateUserProfile(displayName: String)

    suspend fun deleteUser()

    suspend fun startGuestMode(): Result<GuestUser>

    suspend fun isGuestMode(): Boolean

    suspend fun exitGuestMode(): Result<Unit>
}

