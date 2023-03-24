package com.ciaorides.ciaorides.model.request

data class RecentSearchRequest(
    var user_id: Int,
    var type: String,
    var mode: String,
    var from_lat:String,
    var from_lng:String,
)