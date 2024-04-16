package com.ciaorides.ciaorides.model.request

data class OfferARideRequest(
    val amount_per_head: String,
    val from_address: String,
    val from_lat: String,
    val from_lng: String,
    val gender: String,
    val middle_seat_empty: String,
    val mode: String,
    val ride_time: String,
    val ride_type: String,
    val seats_available: String,
    val to_address: String,
    val to_lat: String,
    val to_lng: String,
    val user_id: String,
    val vehicle_id: String,
    val vehicle_type: String
)