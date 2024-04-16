package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class GetOnlineMapVehiclesRequest(
    val from_lat: String,
    val from_lng: String,
    val radius: String,
    val user_id: String
) : Parcelable