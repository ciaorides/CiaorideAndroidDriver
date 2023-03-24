package com.ciaorides.ciaorides.model.response


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SharingAvailabilityResponse(
    val message: String,
    val response: List<Response>,
    val status: Boolean,
    val time: String
) : Parcelable {
    @Parcelize
    data class Response(
        val alternate_number: String,
        val amount: Int,
        val amount_per_head: String,
        val base_fare: String,
        val bio: String,
        val car_type: String,
        val ciao_commission: Int,
        val color: String,
        val country: String,
        val dob: String,
        val driver_status: String,
        val email_id: String,
        val first_name: String,
        val from_address: String,
        val from_lat: String,
        val from_lng: String,
        val gender: String,
        val id: String,
        val last_name: String,
        val middle_seat_empty: String,
        val mobile: String,
        val mode: String,
        val number_plate: String,
        val order_id: Int,
        val payment_gateway_commision: Int,
        val profile_percentage: Int,
        val profile_pic: String,
        val ride_time: String,
        val ride_type: String,
        val rider_gender: String,
        val seats_available: String,
        val seats_booked: String,
        val status: String,
        val tax: Double,
        val time_diff: String,
        val title: String,
        val to_address: String,
        val to_lat: String,
        val to_lng: String,
        val total_amount: Double,
        val trip_distance: String,
        val trip_id: String,
        val user_id: String,
        val vehicle_id: String,
        val vehicle_make: String,
        val vehicle_model: String,
        val vehicle_picture: String,
        val vehicle_type: String,
        val year: String
    ) : Parcelable
}