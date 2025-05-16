package com.mKanta.archivemaps.data.repository

import android.content.Context
import android.util.Log
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val context: Context,
        private val supabaseClient: io.github.jan.supabase.SupabaseClient,
        private val sessionStore: SessionStore,
    ) : AuthRepository {
        override suspend fun signUp(
            email: String,
            password: String,
        ): Result<String> =
            try {
                supabaseClient.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                val user = supabaseClient.auth.currentUserOrNull()
                if (user != null) {
                    Result.success(
                        context.getString(R.string.auth_signup_success_with_id, user.id),
                    )
                } else {
                    Result.failure(Exception(context.getString(R.string.auth_already)))
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", context.getString(R.string.auth_debug_signup_failed), e)
                Result.failure(e)
            }

        override suspend fun signIn(
            email: String,
            password: String,
        ): Result<String> =
            try {
                supabaseClient.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                val user = supabaseClient.auth.currentUserOrNull()
                val session = supabaseClient.auth.currentSessionOrNull()

                if (user != null && session != null) {
                    sessionStore.save(session)
                    Result.success(context.getString(R.string.auth_login_success_with_id, user.id))
                } else {
                    Result.failure(Exception(context.getString(R.string.auth_mail)))
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", context.getString(R.string.auth_debug_signin_failed), e)
                Result.failure(Exception(context.getString(R.string.auth_error)))
            }

        override suspend fun signOut(): Result<Unit> =
            try {
                supabaseClient.auth.signOut()
                sessionStore.clear()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("AuthRepository", context.getString(R.string.auth_debug_signout_failed), e)
                Result.failure(e)
            }

        override suspend fun getCurrentUserId(): String? = supabaseClient.auth.currentUserOrNull()?.id

        override suspend fun isAuthenticated(): Boolean =
            try {
                val currentUser = supabaseClient.auth.currentUserOrNull()
                val currentSession = supabaseClient.auth.currentSessionOrNull()

                if (currentUser != null && currentSession != null) {
                    val now = Clock.System.now()
                    if (currentSession.expiresAt > now) {
                        true
                    } else {
                        sessionStore.clear()
                        false
                    }
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", context.getString(R.string.auth_debug_auth_check_failed), e)
                false
            }
    }
