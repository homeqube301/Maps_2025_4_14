package com.mKanta.archivemaps.data.repository

import android.content.Context
import androidx.annotation.StringRes
import com.mKanta.archivemaps.domain.repository.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProviderImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ResourceProvider {
        override fun getString(
            @StringRes resId: Int,
        ): String = context.getString(resId)
    } 
