package com.ciaorides.ciaorides.model.response

import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class VehicleModelsResponse(
    val message: String,
    val response: List<Response>,
    val status: Boolean
) : Serializable {
    data class Response(
        val created_on: String,
        val id: String,
        val make_id: String,
        val modified_on: String,
        val title: String
    )
}