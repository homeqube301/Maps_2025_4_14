package com.mKanta.archivemaps.di

import android.content.Context
import com.mKanta.archivemaps.BuildConfig
import com.mKanta.archivemaps.data.local.MarkerMapper
import com.mKanta.archivemaps.data.repository.AuthRepositoryImpl
import com.mKanta.archivemaps.data.repository.MarkerDBRepository
import com.mKanta.archivemaps.data.repository.MarkerDBRepositoryImpl
import com.mKanta.archivemaps.data.repository.SessionStore
import com.mKanta.archivemaps.domain.repository.AuthRepository
import com.mKanta.archivemaps.network.SupabaseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient =
        createSupabaseClient(
            supabaseUrl = "https://tnhcquguhtwjpzixskrw.supabase.co",
            supabaseKey = BuildConfig.SUPABASE_API_KEY,
        ) {
            install(Auth)
            install(Postgrest)
        }

    @Provides
    @Singleton
    fun provideSessionStore(
        @ApplicationContext context: Context,
    ): SessionStore = SessionStore(context)

    @Provides
    @Singleton
    fun provideAuthRepository(
        supabaseClient: SupabaseClient,
        sessionStore: SessionStore,
        supabaseApi: SupabaseApi,
    ): AuthRepository = AuthRepositoryImpl(supabaseClient, sessionStore, supabaseApi)

    @Provides
    @Singleton
    fun provideMarkerDBRepository(
        supabaseClient: SupabaseClient,
        markerMapper: MarkerMapper,
    ): MarkerDBRepository = MarkerDBRepositoryImpl(supabaseClient, markerMapper)
}
