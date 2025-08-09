package me.vosaa.shouldiride.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.vosaa.shouldiride.data.remote.WeatherApiService
import me.vosaa.shouldiride.data.repository.WeatherRepositoryImpl
import me.vosaa.shouldiride.domain.repository.WeatherRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Hilt module that wires up networking primitives (OkHttp, Retrofit) and
 * provides the app's [WeatherRepository] implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides an [HttpLoggingInterceptor] configured at BODY level to aid
     * debugging network requests in development.
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Provides a configured [OkHttpClient] with logging for Retrofit.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Provides a [Retrofit] instance pointing to OpenWeather's base URL.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Creates the Retrofit-backed [WeatherApiService].
     */
    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    /**
     * Binds the concrete [WeatherRepositoryImpl] to the domain [WeatherRepository]
     * interface for DI.
     */
    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: WeatherApiService
    ): WeatherRepository {
        return WeatherRepositoryImpl(apiService)
    }
}