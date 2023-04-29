package com.ciaorides.ciaorides.model.response

import java.io.Serializable

data class EarningsResponse(

    val status: Boolean,
    val message: String,
    val response: List<EarningsData>
) : Serializable {
    data class EarningsData(
        val Ride_Date: String,
        val Ride_Time: String,
        val Final_Amout: String
    ) : Serializable
}



