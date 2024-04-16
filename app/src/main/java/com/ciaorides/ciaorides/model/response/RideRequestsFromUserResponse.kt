package com.ciaorides.ciaorides.model.response

data class RideRequestsFromUserResponse(
    val message: String,
    val response: List<Response>,
    val status: Boolean
) {
    data class Response(
        val accepted_date: Any,
        val average_rating: String,
        val first_name: String,
        val from_address: String,
        val last_name: String,
        val profile_pic: String,
        val total_amount: String,
        val trip_distance: String,
        val user_id: String
    )
}