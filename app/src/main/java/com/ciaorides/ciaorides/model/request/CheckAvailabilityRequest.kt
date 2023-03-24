package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CheckAvailabilityRequest(
    val from_address: String,
    val from_lat: String,
    val from_lng: String,
    val gender: String,
    val mode: String,
    val ride_time: String,
    val ride_type: String,
    val seats_required: String,
    val sub_vehicle_type: String,
    val to_address: String,
    val to_lat: String,
    val to_lng: String,
    val user_id: String,
    val vehicle_type: String
) : Parcelable

