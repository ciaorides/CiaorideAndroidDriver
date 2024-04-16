package com.ciaorides.ciaorides.model.request

data class AcceptOrderRequest(
    val booking_id: String,
    val order_id: String,
    val ride_id: String,
    val rider_id: String,
    val rider_name: String,
    val user_id: String,
    val vehicle_id: String
)