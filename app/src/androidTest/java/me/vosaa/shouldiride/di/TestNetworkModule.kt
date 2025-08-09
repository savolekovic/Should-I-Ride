package me.vosaa.shouldiride.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import me.vosaa.shouldiride.data.remote.WeatherApiService
import me.vosaa.shouldiride.data.repository.WeatherRepositoryImpl
import me.vosaa.shouldiride.domain.repository.WeatherRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Hilt test module that replaces the production [NetworkModule] in androidTests.
 * Provides a minimal Retrofit stack suitable for integration tests.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
object TestNetworkModule {
    
    /** Provides a Retrofit instance for tests. */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Provides the API service used by the repository in tests. */
    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    /** Binds the repository implementation for tests. */
    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: WeatherApiService
    ): WeatherRepository {
        return WeatherRepositoryImpl(apiService)
    }
} 