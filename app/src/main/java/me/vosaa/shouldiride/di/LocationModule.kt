package me.vosaa.shouldiride.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides location-related dependencies used by the app.
 *
 * This module exposes a singleton [FusedLocationProviderClient] so that location
 * access is centralized and can be injected wherever needed.
 */
@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    /**
     * Provides a singleton instance of [FusedLocationProviderClient] tied to the
     * application context for retrieving the last known location.
     */
    @Provides
    @Singleton
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
} 