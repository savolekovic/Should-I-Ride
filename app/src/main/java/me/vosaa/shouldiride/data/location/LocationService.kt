package me.vosaa.shouldiride.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Thin abstraction over Google's fused location provider used to check
 * permission/state and fetch the device's last known location.
 */
class LocationService @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) {
    /**
     * Returns true if either fine or coarse location permission is granted and
     * the GPS provider is enabled. This guards location access at runtime.
     */
    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        return (fineLocation || coarseLocation) && isGpsEnabled
    }

    /**
     * Suspends and returns the last known location if available; otherwise null.
     *
     * This method deliberately checks [hasLocationPermission] to avoid
     * SecurityExceptions and uses a cancellable coroutine to bridge the
     * Task-based fused location API to Kotlin coroutines.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData? {
        if (!hasLocationPermission()) {
            return null
        }

        // Convert the Task<Location> callback style into a suspend function
        return suspendCancellableCoroutine { cont ->
            locationClient.lastLocation.apply {
                if (isComplete) {
                    if (isSuccessful) {
                        result?.let {
                            cont.resume(LocationData(it.latitude, it.longitude))
                        } ?: cont.resume(null)
                    } else {
                        cont.resume(null)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    it?.let {
                        cont.resume(LocationData(it.latitude, it.longitude))
                    } ?: cont.resume(null)
                }
                addOnFailureListener {
                    cont.resume(null)
                }
                addOnCanceledListener {
                    cont.resume(null)
                }
            }
        }
    }
}

/**
 * Represents a simple pair of latitude and longitude for the current device location.
 *
 * @property latitude Latitude in decimal degrees
 * @property longitude Longitude in decimal degrees
 */
data class LocationData(
    val latitude: Double,
    val longitude: Double
) 