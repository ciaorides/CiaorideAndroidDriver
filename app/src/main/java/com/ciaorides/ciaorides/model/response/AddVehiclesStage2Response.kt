package com.ciaorides.ciaorides.model.response

data class AddVehiclesStage2Response(
    val message: String,
    val response: Boolean,
    val status: Boolean
)
data class SaveBankResponse(
    val message: String,
    val response: Int,
    val status: Boolean
)
data class SaveBankDetailsRequest(
    val user_id : String,
    val country_id: String,
    val bank_name: String,
    val account_holder_name: String,
    val account_number : String,
    val ifsc_code : String

)