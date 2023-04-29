package com.ciaorides.ciaorides.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmergencyContactResponse(
    val message: String,
    val response: List<Response>,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val id: String,
        val user_id: String,
        val name: String,
        val mobile: String,
        val relation: String,
        val status: String,
        val created_on: String
    ) : Parcelable
}


