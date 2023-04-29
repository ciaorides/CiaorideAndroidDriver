package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AcceptRideRequest(
    val booking_id: String,
    val driver_id: String,
    val order_id: String,
    val user_id: String,
    val vehicle_id: String
) : Parcelable