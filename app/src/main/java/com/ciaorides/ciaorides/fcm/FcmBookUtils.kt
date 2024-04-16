package com.ciaorides.ciaorides.fcm

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FcmBookUtils {
    const val BOOKING = "Booking"
    const val RIDES = "Rides"
    const val SENDERS = "Senders"
    const val ACTIVE_BOOKINGS = "activeBookings"
    const val RIDE_STATUS = "rideStatus"
    const val RIDE_AMOUNT = "amount"
    const val USERS = "users"
    const val DRIVERS = "drivers"
    const val CHAT = "chat"


    fun addBooking(
        driverId: String,
        fcmData: FcmBookingModel,
        driverInfo: BookingFcmData.BookResp.Response
    ) {
        fcmData.driverInfo = driverInfo
        val database = Firebase.database.reference
        database.child(BOOKING).child(RIDES).child(fcmData.bookingNumber).child(driverId)
            .setValue(fcmData)
        database.child(BOOKING).child(ACTIVE_BOOKINGS).child(USERS).child(fcmData.userId)
            .setValue(fcmData.bookingNumber)
        database.child(BOOKING).child(ACTIVE_BOOKINGS).child(DRIVERS).child(driverId)
            .setValue(fcmData.bookingNumber)

    }

    fun removeBooking(
        driverId: String,
        fcmData: FcmBookingModel
    ) {
        val database = Firebase.database.reference
        database.child(BOOKING).child(RIDES).child(fcmData.bookingNumber).child(driverId)
            .setValue(fcmData)
        database.child(BOOKING).child(ACTIVE_BOOKINGS).child(USERS).child(fcmData.userId)
            .removeValue()
        database.child(BOOKING).child(ACTIVE_BOOKINGS).child(DRIVERS).child(driverId)
            .removeValue()

    }

    fun getBookingModel(bookDataResponse: BookingFcmData.BookResp): FcmBookingModel {
        val fcmData = FcmBookingModel()
        fcmData.bookingNumber = bookDataResponse.booking_id.toString()
        fcmData.orderId = bookDataResponse.order_id.toString()
        fcmData.time = bookDataResponse.time
        fcmData.otp = bookDataResponse.otp.toString()
        fcmData.userId = bookDataResponse.user_id.toString()

        return fcmData

    }

    fun addDriversToBooingId(driverId: String, bookingId: String) {
        val database = Firebase.database.reference
        database.child(BOOKING).child(SENDERS).child(bookingId).setValue(driverId)
    }

    fun getBookingFcmRef(driverId: String) =
        Firebase.database.reference.child(BOOKING).child(RIDES)
            .child(driverId)

    fun removeFcmBooingForReject(driverId: String) = getBookingFcmRef(driverId).removeValue()

    fun getBookingSendersFcmRef() = Firebase.database.reference.child(BOOKING).child(SENDERS)

    fun updateApprovedStatus(driverId: String, status: String) {
        Firebase.database.reference.child(BOOKING).child(RIDES)
            .child(driverId).child(RIDE_STATUS).setValue(status)
    }

    fun getBookingChatRef(bookingId: String, chatId: String) =
        Firebase.database.reference
            .child(BOOKING)
            .child(CHAT)
            .child(bookingId)
            .child(chatId)

    fun updateApprovedStatus(bookingId: String, driverId: String, status: String) {
        Firebase.database.reference
            .child(BOOKING)
            .child(RIDES)
            .child(bookingId)
            .child(driverId)
            .child(RIDE_STATUS)
            .setValue(status)
    }
}