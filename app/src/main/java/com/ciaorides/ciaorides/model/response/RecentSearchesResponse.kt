package com.ciaorides.ciaorides.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecentSearchesResponse(
    val message: String,
    val response: Response,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val favorite: List<UserLastData>,
        val recent: List<UserLastData>,
        val schedule: List<UserLastData>
    ) : Parcelable {

        @Parcelize
        data class UserLastData(
            val address: String,
            val created_on: String,
            val id: String,
            val lat: String,
            val lng: String,
            val mode: String,
            val type: String,
            val user_id: String
        ) : Parcelable
    }
}