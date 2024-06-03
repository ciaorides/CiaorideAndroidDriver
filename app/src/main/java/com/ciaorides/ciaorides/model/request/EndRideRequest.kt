package com.ciaorides.ciaorides.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EndRideRequest(
    val to_address: String,
    val to_lat: String,
    val to_lng: String,
    val booking_id: String
): Parcelable
