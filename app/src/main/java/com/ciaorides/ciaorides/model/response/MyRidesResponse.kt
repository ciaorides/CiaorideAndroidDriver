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
            val car_type: String? = null,
            val first_name: String? = null,
            val from_address: String? = null,
            val last_name: String? = null,
            val number_plate: String? = null,
            val order_id: String? = null,
            val rating: String? = null,
            val ride_time: String? = null,
            val to_address: String? = null,
            val total_amount: String? = null,
            val trip_distance: String? = null,
            val user_id: String? = null,
            val vehicle_id: String? = null,
            val ride_type: String? = null
        ) : Parcelable
    }
}