package com.mKanta.archivemaps.domain.model

data class User(
    val id: String,
    val email: String?,
    val userMetadata: Map<String, Any>?,
)