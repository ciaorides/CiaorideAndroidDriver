package com.ciaorides.ciaorides.view.fragments

import android.os.CountDownTimer
import com.ciaorides.ciaorides.databinding.BookRideProgressFragmentBinding


class BookRideProgressFragment {
     var binding: BookRideProgressFragmentBinding?=null
    private var myCountDownTimer: MyCountDownTimer? = null

    fun updateData(
        binding: BookRideProgressFragmentBinding,
    ) {
        this.binding = binding
        myCountDownTimer = MyCountDownTimer(10000, 1000)
        myCountDownTimer?.start()
    }

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            val progress = (millisUntilFinished / 1000).toInt()
            binding?.progressBar?.progress = binding?.progressBar?.max!! - progress
        }

        override fun onFinish() {
            myCountDownTimer?.cancel()
        }
    }
    }

fun updateDriverData(
    binding: BookRideProgressFragmentBinding,
) {
    binding?.let {
    }
}

