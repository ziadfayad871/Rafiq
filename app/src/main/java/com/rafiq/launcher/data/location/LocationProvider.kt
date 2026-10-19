package com.rafiq.launcher.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

data class DetectedLocation(
    val latitude: Double,
    val longitude: Double,
    val label: String
)

class LocationProvider(private val context: Context) {

    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    suspend fun currentLocation(): DetectedLocation? {
        if (!hasPermission()) return null
        val manager = context.getSystemService(LocationManager::class.java) ?: return null
        val lastKnown = lastKnownLocation(manager)
        val fresh = requestFreshLocation(manager)
        val best = fresh ?: lastKnown ?: return null
        val label = geocodeCity(best.latitude, best.longitude) ?: "موقعك الحالي"
        return DetectedLocation(best.latitude, best.longitude, label)
    }

    private fun lastKnownLocation(manager: LocationManager): Location? {
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        return providers.mapNotNull { provider ->
            runCatching {
                @Suppress("MissingPermission")
                manager.getLastKnownLocation(provider)
            }.getOrNull()
        }.maxByOrNull { it.time }
    }

    private suspend fun requestFreshLocation(manager: LocationManager): Location? {
        val providers = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
            .filter { provider ->
                runCatching { manager.isProviderEnabled(provider) }.getOrDefault(false)
            }
        for (provider in providers) {
            val location = runCatching {
                withTimeoutOrNull(12_000) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        awaitCurrentLocation(manager, provider)
                    } else {
                        awaitSingleUpdate(manager, provider)
                    }
                }
            }.getOrNull()
            if (location != null) return location
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun awaitCurrentLocation(
        manager: LocationManager,
        provider: String
    ): Location? = suspendCancellableCoroutine { continuation ->
        val cancellation = android.os.CancellationSignal()
        continuation.invokeOnCancellation { cancellation.cancel() }
        runCatching {
            @Suppress("MissingPermission")
            manager.getCurrentLocation(provider, cancellation, context.mainExecutor) { location ->
                if (continuation.isActive) continuation.resume(location)
            }
        }.onFailure {
            if (continuation.isActive) continuation.resume(null)
        }
    }

    private suspend fun awaitSingleUpdate(
        manager: LocationManager,
        provider: String
    ): Location? = suspendCancellableCoroutine { continuation ->
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                runCatching { manager.removeUpdates(this) }
                if (continuation.isActive) continuation.resume(location)
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
            override fun onProviderEnabled(provider: String) = Unit
            override fun onProviderDisabled(provider: String) = Unit
        }
        continuation.invokeOnCancellation {
            runCatching { manager.removeUpdates(listener) }
        }
        runCatching {
            @Suppress("MissingPermission")
            manager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
        }.onFailure {
            if (continuation.isActive) continuation.resume(null)
        }
    }

    private suspend fun geocodeCity(latitude: Double, longitude: Double): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!Geocoder.isPresent()) return@runCatching null
                @Suppress("DEPRECATION")
                val results = Geocoder(context, Locale("ar"))
                    .getFromLocation(latitude, longitude, 1)
                results?.firstOrNull()?.let { address ->
                    address.locality
                        ?: address.subAdminArea
                        ?: address.adminArea
                }
            }.getOrNull()
        }
}
