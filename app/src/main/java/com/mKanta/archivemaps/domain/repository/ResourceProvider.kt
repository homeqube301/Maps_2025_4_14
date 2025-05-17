package com.mKanta.archivemaps.domain.repository

import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(
        @StringRes resId: Int,
    ): String
} 
