package com.ciaorides.ciaorides.model.request

data class CompleteOfferRideRequest(
    val amount: String,
    val booking_id: String,
    val order_id: String,
    val rider_id: String,
    val user_id: String,
    val transaction_id: String,
    val payment_gateway_provider: String,
    val payment_status: String,
    val ride_start_time: String,
    val ride_end_time: String,
    val payment_type: String
)