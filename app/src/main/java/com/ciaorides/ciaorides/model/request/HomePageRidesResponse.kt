package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class HomePageRidesResponse(
    val message: String,
    val response: Response,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val previous_booking_data: List<PreviousBookingData>,
        val total_bookings: String,
        val total_earnings: String
    ) : Parcelable {
        @Parcelize
        data class PreviousBookingData(
            val ride_time: String,
            val total_amount: String,
            val trip_distance: String
        ) : Parcelable
    }
}