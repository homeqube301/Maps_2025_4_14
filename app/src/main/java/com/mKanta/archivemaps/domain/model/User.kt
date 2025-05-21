package com.mKanta.archivemaps.domain.model

import java.util.UUID

data class User(
    val id: String,
    val email: String?,
    val userMetadata: Map<String, Any>?,
)

data class GuestUser(
    val id: String = "guest_${UUID.randomUUID()}",
    val isGuest: Boolean = true,
)
