package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
 data class RidesSharingRequest(
    val from_lat: String,
    val from_lng: String,
    val to_lat: String,
    val to_lng: String,
    val travel_type: String,
    val user_id: String
) : Parcelable