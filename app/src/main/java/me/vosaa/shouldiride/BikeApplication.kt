package me.vosaa.shouldiride

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Custom [Application] that initializes Hilt's code generation and DI graph.
 */
@HiltAndroidApp
class BikeApplication : Application() 