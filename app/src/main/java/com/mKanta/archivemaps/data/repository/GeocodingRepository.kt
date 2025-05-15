package com.mKanta.archivemaps.data.repository

import com.mKanta.archivemaps.network.NominatimApiService
import com.mKanta.archivemaps.network.NominatimResponse
import javax.inject.Inject

interface GeocodingRepository {
    suspend fun reverseGeocode(
        lat: Double,
        lon: Double,
    ): Result<NominatimResponse>
}

class GeocodingRepositoryImpl
    @Inject
    constructor(
        private val apiService: NominatimApiService,
    ) : GeocodingRepository {
        override suspend fun reverseGeocode(
            lat: Double,
            lon: Double,
        ): Result<NominatimResponse> =
            runCatching {
                apiService.reverseGeocode(lat, lon)
            }
    }
