package com.ciaorides.ciaorides.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VehicleBrandsResponse(
    val message: String,
    val response: List<Response>,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val created_on: String,
        val id: String,
        val modified_on: String,
        val title: String,
        val vehicle_type: String
    ) : Parcelable
}