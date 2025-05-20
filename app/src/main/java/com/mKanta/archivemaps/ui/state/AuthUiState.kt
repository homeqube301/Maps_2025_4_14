package com.mKanta.archivemaps.ui.state

data class AuthUiState(
    val isLoading: AccountLoadingState = AccountLoadingState.Success(loadingReady = true),
    val isSignOut: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val accountName: String = "",
    val accountId: String = "",
    val email: String = "",
)

sealed interface AccountLoadingState {
    data object Loading : AccountLoadingState

    data class Success(
        val loadingReady: Boolean,
    ) : AccountLoadingState

    data class Error(
        val message: String? = null,
    ) : AccountLoadingState
}
