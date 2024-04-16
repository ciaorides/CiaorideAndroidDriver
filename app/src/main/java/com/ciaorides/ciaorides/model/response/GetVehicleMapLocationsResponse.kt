package com.ciaorides.ciaorides.model.response


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class GetVehicleMapLocationsResponse(
    val message: String,
    val response: List<Response>,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val distance: String,
        val driver_id: String,
        val lat: String,
        val lng: String,
        val vehicle_id: String,
        val vehicle_type: String
    ) : Parcelable
}