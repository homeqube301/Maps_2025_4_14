package com.mKanta.archivemaps.data.local

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.domain.model.NamedMarker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkerMapper
    @Inject
    constructor() {
        fun toDomain(dto: MemoEmbeddingDto): NamedMarker =
            NamedMarker(
                id = dto.markerId,
                memo = dto.memo,
                position = LatLngSerializable(dto.positionLat, dto.positionLng),
                title = dto.title,
                colorHue = dto.colorHue ?: BitmapDescriptorFactory.HUE_RED,
                createdAt = dto.createdMarker ?: "",
            )
    }
