package com.mKanta.archivemaps.ui.stateholder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mKanta.archivemaps.data.repository.StringResourceException
import com.mKanta.archivemaps.domain.repository.AuthRepository
import com.mKanta.archivemaps.domain.repository.ResourceProvider
import com.mKanta.archivemaps.ui.state.AccountLoadingState
import com.mKanta.archivemaps.ui.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

        private fun setLoadingState() {
            _uiState.update { it.copy(isLoading = AccountLoadingState.Loading) }
        }

        private fun setSuccessState() {
            _uiState.update { it.copy(isLoading = AccountLoadingState.Success(true)) }
        }

        private fun setErrorState(message: String?) {
            _uiState.update { it.copy(isLoading = AccountLoadingState.Error(message)) }
        }

        fun changeAccountName(newAccountName: String) {
            viewModelScope.launch {
                setLoadingState()
                try {
                    authRepository.updateUserProfile(newAccountName)
                    _uiState.update {
                        it.copy(
                            accountName = newAccountName,
                        )
                    }
                    setSuccessState()
                } catch (e: Exception) {
                    setErrorState(e.message)
                }
            }
        }

        fun deleteAccount() {
            viewModelScope.launch {
                setLoadingState()
                try {
                    authRepository.deleteUser()
                    _uiState.update {
                        it.copy(
                            isAuthenticated = false,
                            isSignOut = true,
                            accountName = "",
                            accountId = "",
                            email = "",
                        )
                    }
                    setSuccessState()
                } catch (e: Exception) {
                    setErrorState(e.message)
                }
            }
        }

        fun loadAccountInfo() {
            viewModelScope.launch {
                setLoadingState()
                try {
                    val user = authRepository.getCurrentUser()
                    user?.let { currentUser ->
                        _uiState.update {
                            it.copy(
                                accountName = currentUser.userMetadata?.get("name") as? String ?: "",
                                accountId = currentUser.id,
                                email = currentUser.email ?: "",
                            )
                        }
                        setSuccessState()
                    }
                } catch (e: Exception) {
                    setErrorState(e.message)
                }
            }
        }

        fun signUp(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                setLoadingState()
                authRepository
                    .signUp(email, password)
                    .onSuccess { messageResId ->
                        setErrorState(resourceProvider.getString(messageResId))
                    }.onFailure { exception ->
                        setErrorState(
                            when (exception) {
                                is StringResourceException ->
                                    resourceProvider.getString(exception.resourceId)

                                else -> exception.message
                            },
                        )
                    }
            }
        }

        fun signIn(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                setLoadingState()
                authRepository
                    .signIn(email, password)
                    .onSuccess { messageResId ->
                        _uiState.update {
                            it.copy(
                                isAuthenticated = true,
                            )
                        }
                        setSuccessState()
                    }.onFailure { exception ->
                        setErrorState(
                            when (exception) {
                                is StringResourceException ->
                                    resourceProvider.getString(exception.resourceId)

                                else -> exception.message
                            },
                        )
                    }
            }
        }

        fun signOut() {
            viewModelScope.launch {
                setLoadingState()
                authRepository
                    .signOut()
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isAuthenticated = false,
                                isSignOut = true,
                                isLoading = AccountLoadingState.Success(true),
                            )
                        }
                    }.onFailure { exception ->
                        setErrorState(
                            when (exception) {
                                is StringResourceException ->
                                    resourceProvider.getString(exception.resourceId)

                                else -> exception.message
                            },
                        )
                    }
            }
        }
    }
