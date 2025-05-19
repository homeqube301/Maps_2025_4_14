package com.mKanta.archivemaps.ui.state

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSignOutLoading: Boolean = false,
    val isSignOutSuccess: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val accountName: String = "",
    val accountId: String = "",
    val email: String = "",
)
