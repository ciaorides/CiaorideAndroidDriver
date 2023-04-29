package com.ciaorides.ciaorides.model.request

import okhttp3.MultipartBody

data class ImageUploadRequest(
    var upload_type:String,
    val image: ArrayList<MultipartBody.Part>
)