package com.mKanta.archivemaps.data.repository

import android.util.Log
import androidx.annotation.StringRes
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.repository.AuthRepository
import com.mKanta.archivemaps.domain.repository.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.datetime.Clock
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

class StringResourceException(
    @StringRes val resourceId: Int,
) : Exception()

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val supabaseClient: SupabaseClient,
        private val sessionStore: SessionStore,
    ) : AuthRepository {
        override suspend fun signUp(
            email: String,
            password: String,
        ): Result<Int> =
            try {
                supabaseClient.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                val user = supabaseClient.auth.currentUserOrNull()
                if (user != null) {
                    Result.success(R.string.auth_check)
                } else {
                    Result.failure(StringResourceException(R.string.auth_already))
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "サインアップ失敗", e)
                Result.failure(e)
            }

        override suspend fun signIn(
            email: String,
            password: String,
        ): Result<Int> =
            try {
                supabaseClient.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                val user = supabaseClient.auth.currentUserOrNull()
                val session = supabaseClient.auth.currentSessionOrNull()

                if (user != null && session != null) {
                    sessionStore.save(session)
                    Result.success(R.string.auth_login_success)
                } else {
                    Result.failure(StringResourceException(R.string.auth_mail))
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "サインイン失敗", e)
                Result.failure(StringResourceException(R.string.auth_error))
            }

        override suspend fun signOut(): Result<Unit> =
            try {
                supabaseClient.auth.signOut()
                sessionStore.clear()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("AuthRepository", "サインアウト失敗", e)
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
                Log.e("AuthRepository", "認証状態の確認に失敗しました", e)
                false
            }

        override suspend fun getCurrentUser(): User? =
            try {
                val user = supabaseClient.auth.currentUserOrNull()
                user?.let {
                    User(
                        id = it.id,
                        email = it.email,
                        userMetadata = it.userMetadata,
                    )
                }
            } catch (e: Exception) {
                null
            }

        override suspend fun updateUserProfile(displayName: String) {
            try {
                supabaseClient.auth.updateUser {
                    data =
                        buildJsonObject {
                            put("name", displayName)
                        }
                }
            } catch (e: Exception) {
                throw Exception("プロフィールの更新に失敗しました")
            }
        }

        override suspend fun deleteUser() {
            try {
                val currentUserId =
                    supabaseClient.auth.currentUserOrNull()?.id
                        ?: throw Exception("ユーザーが見つかりません")

                supabaseClient.auth.signOut()
                sessionStore.clear()
        } catch (e: Exception) {
            throw Exception("アカウントの削除に失敗しました")
        }
    }
    }
