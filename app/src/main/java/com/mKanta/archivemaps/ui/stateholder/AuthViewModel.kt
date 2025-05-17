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

        init {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(isAuthenticated = authRepository.isAuthenticated())
                }
            }
        }
    }
