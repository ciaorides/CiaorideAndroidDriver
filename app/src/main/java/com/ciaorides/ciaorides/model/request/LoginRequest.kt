package com.ciaorides.ciaorides.model.request

data class LoginRequest(
    val mobile: String,
    val otp_confirmed: String,
    val token: String
)
data class ChangePassword(
    val user_id: String,
    val old_password: String,
    val new_password: String
)