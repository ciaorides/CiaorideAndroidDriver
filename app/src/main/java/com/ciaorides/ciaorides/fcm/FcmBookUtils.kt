package com.ciaorides.ciaorides.fcm

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FcmBookUtils {
    const val BOOKING = "Booking"
    const val RIDES = "Rides"
    const val SENDERS = "Senders"
    const val RIDE_STATUS = "rideStatus"
    const val ACTIVE_BOOKINGS = "activeBookings"
    const val USERS = "users"
    const val DRIVERS = "drivers"
    const val CHAT = "chat"

    private fun getBookingFcmRef(bookingId: String, driverId: String) =
        Firebase.database.reference.child(BOOKING).child(bookingId).child(RIDES)
            .child(driverId)

    fun removeFcmBooingForReject(bookingId: String, driverId: String) =
        getBookingFcmRef(bookingId, driverId).removeValue()

    fun getBookingSendersFcmRef() = Firebase.database.reference.child(BOOKING).child(SENDERS)

    fun updateApprovedStatus(bookingId: String, driverId: String, status: String) {
        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(bookingId)
            .child(driverId)
            .child(RIDE_STATUS)
            .setValue(status)
    }

    fun getBookingChatRef(bookingId: String, chatId: String) =
        Firebase.database.reference
            .child(BOOKING)
            .child(CHAT)
            .child(bookingId)
            .child(chatId)


}