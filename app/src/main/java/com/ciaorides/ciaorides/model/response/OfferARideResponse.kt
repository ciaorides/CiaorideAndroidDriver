package com.ciaorides.ciaorides.model.response

data class OfferARideResponse(
    val message: String,
    val response: Response,
    val status: Boolean
) {
    data class Response(
        val amount_per_head: String,
        val created_on: String,
        val driver_status: String,
        val from_address: String,
        val from_lat: String,
        val from_lng: String,
        val gender: String,
        val id: String,
        val message: Any,
        val middle_seat_empty: String,
        val mode: String,
        val modified_on: Any,
        val note: Any,
        val ride_end_time: Any,
        val ride_time: String,
        val ride_type: String,
        val rider_id: Any,
        val seats: Any,
        val seats_available: String,
        val status: String,
        val to_address: String,
        val to_lat: String,
        val to_lng: String,
        val trip_distance: String,
        val trip_id: String,
        val type: String,
        val user_id: String,
        val vehicle_id: String,
        val vehicle_type: String
    )
}