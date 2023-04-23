package com.ciaorides.ciaorides.model

import com.google.gson.JsonObject
import retrofit2.Response

interface AddVehicleImageUpload {
    fun imageUploadResponseHanding(imageUploadResponse: Response<JsonObject>)
}