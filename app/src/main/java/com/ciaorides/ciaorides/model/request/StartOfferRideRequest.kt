package com.ciaorides.ciaorides.model.request

data class StartOfferRideRequest(
    val amount: String,
    val booking_id: String,
    val order_id: String,
    val ride_id: String,
    val rider_id: String,
    val user_id: String
)