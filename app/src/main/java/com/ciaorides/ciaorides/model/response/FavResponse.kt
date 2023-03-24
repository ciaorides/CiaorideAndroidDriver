package com.ciaorides.ciaorides.model.response


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class FavResponse(
    val message: String,
    val response: List<Response>,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val address: String,
        val created_on: String,
        val id: String,
        val lat: String,
        val lng: String,
        val mode: String,
        val status: String,
        val type: String,
        val user_id: String
    ) : Parcelable
}