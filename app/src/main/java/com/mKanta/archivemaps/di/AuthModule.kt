package com.mKanta.archivemaps.di

import android.content.Context
import com.mKanta.archivemaps.BuildConfig
import com.mKanta.archivemaps.data.repository.AuthRepositoryImpl
import com.mKanta.archivemaps.data.repository.SessionStore
import com.mKanta.archivemaps.domain.repository.AuthRepository
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
        @ApplicationContext context: Context,
        supabaseClient: SupabaseClient,
        sessionStore: SessionStore,
    ): AuthRepository = AuthRepositoryImpl(context, supabaseClient, sessionStore)
}
