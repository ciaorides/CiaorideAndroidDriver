package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class DriverCheckInRequest(
    val check_in_status: String,
    val driver_id: String,
    val from_lat: String,
    val from_lng: String,
    val vehicle_id: String
) : Parcelable