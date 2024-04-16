package com.ciaorides.ciaorides.model.request

data class DeclineOrderRequest(
    val order_id: String,
    val user_id: String
)