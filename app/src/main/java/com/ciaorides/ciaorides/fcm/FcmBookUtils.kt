package com.ciaorides.ciaorides.fcm

import com.ciaorides.ciaorides.model.response.FcmBookingModel
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
    const val DRIVER_CURRENT_LAT="driverCurrentLat"
    const val DRIVER_CURRENT_LONG="driverCurrentLong"

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

    fun removeBooking(
        driverId: String,
        fcmData: FcmBookingModel
    ) {
        val database = Firebase.database.reference
        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(fcmData.bookingNumber.toString())
            .removeValue()
        database.child(BOOKING).child(ACTIVE_BOOKINGS).child(USERS).child(fcmData.userId)
            .removeValue()
        database.child(BOOKING).child(ACTIVE_BOOKINGS).child(DRIVERS).child(driverId)
            .removeValue()

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

    fun updateAmount(bookingId: String, driverId: String, amount: String) {
        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(bookingId)
            .child(driverId)
            .child(RIDE_AMOUNT)
            .setValue(amount)
    }

    fun getBookingChatRef(bookingId: String, chatId: String) =
        Firebase.database.reference
            .child(BOOKING)
            .child(CHAT)
            .child(bookingId)
            .child(chatId)

    fun updateDriverLocation(bookingId: String, driverId: String, driverLat: Double, driverLong: Double) {
        val map = HashMap<String, Double>()
        map["DRIVER_CURRENT_LAT"] = driverLat
        map["DRIVER_CURRENT_LONG"] = driverLong
        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(bookingId)
            .child(driverId)
            .updateChildren(map as Map<String, Double>)
    }
}