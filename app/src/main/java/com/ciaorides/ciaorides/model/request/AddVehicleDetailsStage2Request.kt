package com.ciaorides.ciaorides.model.request

data class AddVehicleDetailsStage2Request(
    var vehicle_id: String,
    var vehicle_registration_number: String,
    var fitness_certification_number: String,
    var vehicle_insurance_number: String,
    var vehicle_permit_number: String,
    var vehicle_registration_image: String,
    var vehicle_insurance_image: String,
    var vehicle_permit_image: String,
    var fitness_certification_image: String

)
