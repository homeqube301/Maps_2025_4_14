package com.mKanta.archivemaps.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mKanta.archivemaps.BuildConfig
import com.mKanta.archivemaps.data.local.MarkerLocalDataSource
import com.mKanta.archivemaps.data.repository.MarkerRepository
import com.mKanta.archivemaps.data.repository.MarkerRepositoryImpl
import com.mKanta.archivemaps.network.OpenAiApi
import com.mKanta.archivemaps.network.SupabaseApi
import com.mKanta.archivemaps.network.provideOpenAiApi
import com.mKanta.archivemaps.network.provideSupabaseApi
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

    @Provides
    @Singleton
    fun provideOpenAiApi(): OpenAiApi {
        val apiKey = BuildConfig.OPENAI_API_KEY
        return provideOpenAiApi(apiKey)
    }

    @Provides
    @Singleton
    fun provideSupabaseApi(): SupabaseApi {
        val supabaseUrl = "https://tnhcquguhtwjpzixskrw.supabase.co"
        val apiKey = BuildConfig.SUPABASE_API_KEY
        return provideSupabaseApi(supabaseUrl, apiKey)
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMarkerRepository(markerRepositoryImpl: MarkerRepositoryImpl): MarkerRepository
}
