package com.ciaorides.ciaorides.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationsData(
    val latLong: LatLng? = null,
    var address: String? = null,
) : Parcelable