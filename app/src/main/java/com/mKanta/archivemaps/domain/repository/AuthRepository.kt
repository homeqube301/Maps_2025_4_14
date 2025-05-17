package com.mKanta.archivemaps.domain.repository

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
}
