package com.ciaorides.ciaorides.fcm

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FcmBookUtils {
    const val BOOKING = "Booking"
    const val RIDES = "Rides"
    const val SENDERS = "Senders"
    const val RIDE_STATUS = "rideStatus"
    const val RIDE_AMOUNT = "amount"
    const val RIDE_FROM_LAT = "userFromLat"
    const val RIDE_FROM_LNG = "userFromLng"
    const val RIDE_To_LAT = "userToLat"
    const val RIDE_To_LNG = "userToLng"
    const val USER_MOBILENUMBER = "userMobile"
    const val USER_NAME = "userName"
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

    fun updateAmountLatLng(bookingId: String, driverId: String, amount: String,
                           fromLat: String, fromLng: String, toLat:String, toLng:String, mobile:String, name: String) {
        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(bookingId)
            .child(driverId)
            .child(RIDE_AMOUNT)
            .setValue(amount)

        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(bookingId)
            .child(driverId)
            .child(RIDE_FROM_LAT)
            .setValue(fromLat)

        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(bookingId)
            .child(driverId)
            .child(RIDE_FROM_LNG)
            .setValue(fromLng)

        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(bookingId)
            .child(driverId)
            .child(RIDE_To_LAT)
            .setValue(toLat)

        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(bookingId)
            .child(driverId)
            .child(RIDE_To_LNG)
            .setValue(toLng)

        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(bookingId)
            .child(driverId)
            .child(USER_MOBILENUMBER)
            .setValue(mobile)

        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(bookingId)
            .child(driverId)
            .child(USER_NAME)
            .setValue(name)
    }

    fun getBookingChatRef(bookingId: String, chatId: String) =
        Firebase.database.reference
            .child(BOOKING)
            .child(CHAT)
            .child(bookingId)
            .child(chatId)


}