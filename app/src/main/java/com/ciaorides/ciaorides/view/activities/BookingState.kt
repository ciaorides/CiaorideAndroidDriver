package com.ciaorides.ciaorides.view.activities

import com.ciaorides.ciaorides.fcm.BookingFcmData
import com.ciaorides.ciaorides.fcm.FcmBookUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BookingState {
    fun getBookingChanges(callBack: ((BookingFcmData?) -> Unit?)? = null) {
        FcmBookUtils.getBookingFcmRef("2247").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callBack?.invoke(snapshot.getValue(BookingFcmData::class.java))
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}