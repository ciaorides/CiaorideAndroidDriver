package com.ciaorides.ciaorides.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage

interface SmsListener {
    fun messageReceived(messageText: String?)
}

class SmsReceiver : BroadcastReceiver() {
    private val mListener: SmsListener? = null
    var b: Boolean? = null
    var abcd: String? = null
    var xyz:kotlin.String? = null

    override fun onReceive(p0: Context?, intent: Intent?) {
        val data = intent!!.extras
        val pdus = data!!["pdus"] as Array<Any>?
        if (pdus != null) {
            for (i in 0 until pdus.size){
                val smsMessage: SmsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                val sender = smsMessage.displayOriginatingAddress
                val messageBody = smsMessage.messageBody
                abcd = messageBody.replace("[^0-9]", "");
                if (b == true){
                    mListener?.messageReceived(abcd)
                }
            }

        }
    }
}