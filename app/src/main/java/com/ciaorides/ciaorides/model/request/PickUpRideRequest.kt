package com.ciaorides.ciaorides.model.request

data class PickUpRideRequest(
    var rider_id: String,
    var vehicle_id: String,
    var order_id: String
)
