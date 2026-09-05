package com.example.safesos.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import java.util.Locale

data class ResolvedLocation(
    val location: Location,
    val addressLine: String
)

/**
 * Thin wrapper around FusedLocationProviderClient + Geocoder so the UI layer
 * doesn't deal with Play Services callbacks directly. Callers are expected to
 * have already checked/requested the location permission.
 */
class LocationHelper(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        return try {
            fusedClient.getCurrentLocation(request, null).await()
        } catch (e: Exception) {
            null
        }
    }

    /** Reverse-geocodes a location into a short, human-readable address line. */
    suspend fun reverseGeocode(location: Location): String {
        return try {
            @Suppress("DEPRECATION")
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val results = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = results?.firstOrNull()
            if (address != null) {
                listOfNotNull(
                    address.thoroughfare,
                    address.subLocality ?: address.locality,
                    address.countryName
                ).joinToString(", ").ifBlank {
                    "Lat ${"%.5f".format(location.latitude)}, Lng ${"%.5f".format(location.longitude)}"
                }
            } else {
                "Lat ${"%.5f".format(location.latitude)}, Lng ${"%.5f".format(location.longitude)}"
            }
        } catch (e: Exception) {
            "Lat ${"%.5f".format(location.latitude)}, Lng ${"%.5f".format(location.longitude)}"
        }
    }

    suspend fun getResolvedLocation(): ResolvedLocation? {
        val loc = getCurrentLocation() ?: return null
        val address = reverseGeocode(loc)
        return ResolvedLocation(loc, address)
    }
}
