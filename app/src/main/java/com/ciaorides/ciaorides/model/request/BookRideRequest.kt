package com.ciaorides.ciaorides.model.request
import android.os.Parcelable

import kotlinx.parcelize.Parcelize


@Parcelize
data class BookRideRequest(
    var from_address: String,
    var from_lat: String,
    var from_lng: String,
    var gender: String,
    var mode: String,
    var ride_time: String,
    var ride_type: String,
    var seats_required: String,
    var sub_vehicle_type: String,
    var to_address: String,
    var to_lat: String,
    var to_lng: String,
    var user_id: String,
    var vehicle_type: String
) : Parcelable