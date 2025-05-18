package com.mKanta.archivemaps.ui.stateholder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mKanta.archivemaps.data.repository.StringResourceException
import com.mKanta.archivemaps.domain.repository.AuthRepository
import com.mKanta.archivemaps.domain.repository.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val accountName: String = "",
    val accountId: String = "",
    val email: String = "",
)

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val resourceProvider: ResourceProvider,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AuthUiState())
        val uiState: StateFlow<AuthUiState> = _uiState

        init {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(isAuthenticated = authRepository.isAuthenticated())
                }
                if (authRepository.isAuthenticated()) {
                    loadAccountInfo()
                }
            }
        }

        fun changeAccountName(newAccountName: String) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                try {
                    authRepository.updateUserProfile(newAccountName)
                    _uiState.update {
                        it.copy(
                            accountName = newAccountName,
                            isLoading = false,
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            error = e.message,
                            isLoading = false,
                        )
                    }
                }
            }
        }

        fun deleteAccount() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                try {
                    authRepository.deleteUser()
                    _uiState.update {
                        it.copy(
                            isAuthenticated = false,
                            isLoading = false,
                            accountName = "",
                            accountId = "",
                            email = "",
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            error = e.message,
                            isLoading = false,
                        )
                    }
                }
            }
        }

        fun loadAccountInfo() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                try {
                    val user = authRepository.getCurrentUser()
                    user?.let { currentUser ->
                        _uiState.update {
                            it.copy(
                                accountName = currentUser.userMetadata?.get("name") as? String ?: "",
                                accountId = currentUser.id,
                                email = currentUser.email ?: "",
                                isLoading = false,
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            error = e.message,
                            isLoading = false,
                        )
                    }
                }
            }
        }

        fun signUp(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                authRepository
                    .signUp(email, password)
                    .onSuccess { messageResId ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = resourceProvider.getString(messageResId),
                            )
                        }
                    }.onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error =
                                    when (exception) {
                                        is StringResourceException ->
                                            resourceProvider.getString(
                                                exception.resourceId,
                                            )

                                        else -> exception.message
                                    },
                            )
                        }
                    }
            }
        }

        fun signIn(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                authRepository
                    .signIn(email, password)
                    .onSuccess { messageResId ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isAuthenticated = true,
                                error = null,
                            )
                        }
                    }.onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error =
                                    when (exception) {
                                        is StringResourceException ->
                                            resourceProvider.getString(
                                                exception.resourceId,
                                            )

                                        else -> exception.message
                                    },
                            )
                        }
                    }
            }
        }

        fun signOut() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                authRepository
                    .signOut()
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isAuthenticated = false,
                            )
                        }
                    }.onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error =
                                    when (exception) {
                                        is StringResourceException ->
                                            resourceProvider.getString(
                                                exception.resourceId,
                                            )

                                        else -> exception.message
                                    },
                            )
                        }
                    }
            }
        }
    }
