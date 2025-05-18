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

    suspend fun getCurrentUser(): User?

    suspend fun updateUserProfile(displayName: String)

    suspend fun deleteUser()
}

data class User(
    val id: String,
    val email: String?,
    val userMetadata: Map<String, Any>?,
)
