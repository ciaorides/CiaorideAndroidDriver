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
        var previous_booking_data: List<PreviousBookingData>?,
        val total_bookings: String?,
        val total_earnings: String?,
        val total_amount: String?,
        val total_distance: String?
    ) : Parcelable {
        @Parcelize
        data class PreviousBookingData(
            var ride_time: String?,
            var total_amount: String?,
            var trip_distance: String?
        ) : Parcelable
    }
}