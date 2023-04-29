package com.ciaorides.ciaorides.model.request

data class AddVehicleDetailsRequest(
    var brand_id:String?,
    var user_id:String,
    var vehicle_type:String?,
    var vehicle_step1:String?,
    var model_id:String?

)
