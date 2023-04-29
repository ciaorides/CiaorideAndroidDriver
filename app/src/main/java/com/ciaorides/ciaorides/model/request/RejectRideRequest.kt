package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class RejectRideRequest(
    val driver_id: String,
    val order_id: String,
    val user_id:String
) : Parcelable