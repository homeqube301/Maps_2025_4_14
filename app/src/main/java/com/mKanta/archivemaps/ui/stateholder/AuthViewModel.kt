package com.mKanta.archivemaps.ui.stateholder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
        @ApplicationContext private val context: Context,
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
                    .onSuccess { message ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = context.getString(R.string.auth_check),
                            )
                        }
                    }.onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message,
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
                    .onSuccess { message ->
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
                                error = exception.message,
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
                                error = exception.message,
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
