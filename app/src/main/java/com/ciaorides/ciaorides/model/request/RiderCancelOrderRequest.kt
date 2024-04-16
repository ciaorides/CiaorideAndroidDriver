package com.ciaorides.ciaorides.model.request

data class RiderCancelOrderRequest(
    val booking_id: String,
    val order_id: String,
    val rider_name: String,
    val user_id: String
)