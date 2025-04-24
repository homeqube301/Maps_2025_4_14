package com.example.maps20250414.di

import android.content.Context
import com.example.maps20250414.data.repository.MarkerRepositoryImpl
import com.example.maps20250414.domain.repoitory.MarkerRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideMarkerRepository(@ApplicationContext context: Context): MarkerRepository {
        return MarkerRepositoryImpl(context)
    }
}