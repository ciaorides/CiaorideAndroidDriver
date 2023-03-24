package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class DeleteVehicleRequest(
    val user_id: String,
    val vehicle_id: String
) : Parcelable