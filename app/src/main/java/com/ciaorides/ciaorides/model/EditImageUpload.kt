package com.ciaorides.ciaorides.model

import com.ciaorides.ciaorides.model.response.ImageUploadResponse
import com.google.gson.JsonObject
import retrofit2.Response

interface EditImageUpload {
    fun imageUploadResponseHanding(imageUploadResponse: Response<JsonObject>)
}