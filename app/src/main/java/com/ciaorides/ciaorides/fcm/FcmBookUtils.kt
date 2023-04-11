package com.ciaorides.ciaorides.fcm

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FcmBookUtils {
    const val BOOKING = "Booking"
    const val RIDES = "Rides"
    const val SENDERS = "Senders"
    const val RIDE_STATUS = "rideStatus"

    fun getBookingFcmRef(driverId: String) =
        Firebase.database.reference.child(BOOKING).child(RIDES)
            .child(driverId)

    fun removeFcmBooingForReject(driverId: String) = getBookingFcmRef(driverId).removeValue()

    fun getBookingSendersFcmRef() = Firebase.database.reference.child(BOOKING).child(SENDERS)

    fun updateApprovedStatus(driverId: String, status: String) {
        Firebase.database.reference.child(BOOKING).child(RIDES)
            .child(driverId).child(RIDE_STATUS).setValue(status)
    }


}