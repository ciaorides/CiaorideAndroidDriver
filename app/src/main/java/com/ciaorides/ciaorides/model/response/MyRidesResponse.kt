package com.ciaorides.ciaorides.model.response


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyRidesResponse(
    val message: String,
    val response: Response,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val rides_scheduled: List<RidesTaken>,
        val rides_taken: List<RidesTaken>,
        val rides_offering: List<RidesTaken>
    ) : Parcelable {

        @Parcelize
        data class RidesTaken(
            val car_type: String,
            val first_name: String,
            val from_address: String,
            val last_name: String,
            val number_plate: String,
            val order_id: String,
            val rating: String,
            val ride_time: String,
            val to_address: String,
            val total_amount: String,
            val trip_distance: String,
            val user_id: String,
            val vehicle_id: String,
            val ride_type: String
        ) : Parcelable
    }
}