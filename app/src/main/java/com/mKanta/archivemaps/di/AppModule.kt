package com.mKanta.archivemaps.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mKanta.archivemaps.data.local.MarkerLocalDataSource
import com.mKanta.archivemaps.data.repository.MarkerRepository
import com.mKanta.archivemaps.data.repository.MarkerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context,
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideMarkerLocalDataSource(
        @ApplicationContext context: Context,
    ): MarkerLocalDataSource = MarkerLocalDataSource(context)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMarkerRepository(markerRepositoryImpl: MarkerRepositoryImpl): MarkerRepository
}
