package com.ciaorides.ciaorides.model.response


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class BookRideResponse(
    val booking_id: Int,
    val message: String,
    val order_id: Int,
    val otp: Int,
    val response: List<Response>,
    val status: Boolean,
    val time: String
) : Parcelable {
    @Parcelize
    data class Response(
        val distance: String,
        val driver_id: String,
        val lat: String,
        val lng: String,
        val mobile: String,
        val vehicle_id: String,
        val vehicle_type: String
    ) : Parcelable
}