package com.ciaorides.ciaorides.view.activities

import android.content.Intent
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityOtpBinding
import com.ciaorides.ciaorides.model.request.LoginRequest
import com.ciaorides.ciaorides.model.request.OtpRequest
import com.ciaorides.ciaorides.model.response.UserResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.fragments.BookRideProgressFragment
import com.ciaorides.ciaorides.viewmodel.OtpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpActivity : BaseActivity<ActivityOtpBinding>() {
    private var timer: CountDownTimer? = null
    var userData: UserResponse? = null
    var phoneNumber: String? = null
    var myCountDownTimer: MyCountDownTimer? = null
    private val viewModel: OtpViewModel by viewModels()
    override fun init() {
        myCountDownTimer = MyCountDownTimer(10000, 1000)
        myCountDownTimer?.start()
        userData = intent.getSerializableExtra(Constants.USER_DATA) as UserResponse
        phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER)

        binding.btnVerify.setOnClickListener {
            if (binding.firstPinView.text?.length == 4) {
                hideKeyboard(this@OtpActivity)
                validateOtp(binding.firstPinView.text.toString())
            } else {
                Toast.makeText(this@OtpActivity, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvResend.setOnClickListener {
            phoneNumber?.let { number ->
                binding.firstPinView.setText("")
                binding.progressBar.root.visibility = View.VISIBLE
                viewModel.resendOtp(OtpRequest(number))
            }
        }
        binding.firstPinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                editable?.let { data ->
                    if (data.length == 4) {
                        hideKeyboard(this@OtpActivity)
                        validateOtp(binding.firstPinView.text.toString())
                    }
                }
            }

        })
        handleApiResponse()
        handleResendResponse()
    }

    private fun handleResendResponse() {
        viewModel.userResponse.observe(this) { resp ->
            when (resp) {
                is DataHandler.SUCCESS -> {
                    binding.progressBar.root.visibility = View.GONE
                    Toast.makeText(this@OtpActivity, resp.data?.message, Toast.LENGTH_SHORT)
                        .show()
                    userData = resp.data
                }
                is DataHandler.ERROR -> {
                    binding.progressBar.root.visibility = View.GONE
                    Toast.makeText(this@OtpActivity, resp.data?.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> binding.progressBar.root.visibility = View.VISIBLE
            }

        }
    }

    private fun handleApiResponse() {
        viewModel.userDetailsList.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    binding.progressBar.root.visibility = View.GONE
                    dataHandler.data?.response?.id?.let { data ->
                        saveUserData(dataHandler.data.response)
                        navigateToNext()
                    }
                }
                is DataHandler.ERROR -> {
                    binding.progressBar.root.visibility = View.GONE
                    Toast.makeText(this@OtpActivity, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> binding.progressBar.root.visibility = View.VISIBLE
            }

        }
    }

    private fun navigateToNext() {
        startActivity(Intent(this@OtpActivity, HomeActivity::class.java))
        finish()
    }

    private fun validateOtp(pinValue: String) {
        userData?.response?.let {
            if (userData?.otp == pinValue.toLong()) {
                if (TextUtils.isEmpty(it.id)) {
                    phoneNumber?.let { number ->
                        binding.progressBar.root.visibility = View.VISIBLE
                        viewModel.validateUser(LoginRequest(number, "Yes"))
                    }
                } else {
                    saveUserData(it)
                    navigateToNext()
                }
            } else {
                Toast.makeText(this@OtpActivity, "Entered OTP is invalid", Toast.LENGTH_SHORT)
                    .show()
                binding.firstPinView.setText("")
            }
        }

    }

    override fun getViewBinding(): ActivityOtpBinding =
        ActivityOtpBinding.inflate(layoutInflater)

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            myCountDownTimer?.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myCountDownTimer?.cancel()
    }

}