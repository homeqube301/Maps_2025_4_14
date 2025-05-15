package com.mKanta.archivemaps.data.repository

import com.mKanta.archivemaps.network.NominatimApiService
import javax.inject.Inject

data class GeocodingResponse(
    val displayName: String?,
)

interface GeocodingRepository {
    suspend fun reverseGeocode(
        lat: Double,
        lon: Double,
    ): Result<GeocodingResponse>
}

class GeocodingRepositoryImpl
    @Inject
    constructor(
        private val apiService: NominatimApiService,
    ) : GeocodingRepository {
        override suspend fun reverseGeocode(
            lat: Double,
            lon: Double,
        ): Result<GeocodingResponse> =
            runCatching {
                val response = apiService.reverseGeocode(lat, lon)
                GeocodingResponse(
                    displayName = response.displayName,
                )
        }
    }
