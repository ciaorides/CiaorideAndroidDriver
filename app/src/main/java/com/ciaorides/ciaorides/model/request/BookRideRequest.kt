package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class BookRideRequest(
    val from_address: String,
    var from_lat: String,
    var from_lng: String,
    val gender: String,
    val mode: String,
    val ride_time: String,
    val ride_type: String,
    val seats_required: String,
    val sub_vehicle_type: String,
    val to_address: String,
    var to_lat: String,
    var to_lng: String,
    val user_id: String,
    val user_type: String,
    val vehicle_type: String
) : Parcelable