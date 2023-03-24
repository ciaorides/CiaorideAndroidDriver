package com.ciaorides.ciaorides.model.request

data class LoginRequest(
    val mobile: String,
    val otp_confirmed: String
)