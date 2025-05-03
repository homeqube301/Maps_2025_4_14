package com.mKanta.archivemaps.data.repository

import com.mKanta.archivemaps.network.NominatimApiService
import javax.inject.Inject

class GeocodingRepository
    @Inject
    constructor(
        private val apiService: NominatimApiService,
    ) {
        fun reverseGeocode(
            lat: Double,
            lon: Double,
        ) = apiService.reverseGeocode(lat, lon)
    }
