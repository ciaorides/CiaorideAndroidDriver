package com.ciaorides.ciaorides.view.activities

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ciaorides.ciaorides.databinding.ActivityLoginBinding
import com.ciaorides.ciaorides.model.request.LoginRequest
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.viewmodel.LoginViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val viewModel: LoginViewModel by viewModels()
    override fun init() {
        fetchFCMToken()
        handleApiResponse()
        binding.btnVerify.setOnClickListener {
            binding.etPhoneNumber.text.toString().let { phoneNumber ->
                if (phoneNumber.length > 9) {
                    binding.progressBar.root.visibility = View.VISIBLE
                    val token =
                        applicationContext.getSharedPreferences(Constants.MAIN_PREF, MODE_PRIVATE).getString(Constants.FCM_TOKEN, "").toString()

                    viewModel.validateUser(
                        LoginRequest(
                            phoneNumber,
                            "No",
                            token = if (TextUtils.isEmpty(token)) Constants.FCM_TOKEN else token
                        )
                    )
                }
            }
        }
        //val data= Constants.getUserInfo(applicationContext)
    }

    private fun fetchFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            val preferences =
                applicationContext.getSharedPreferences(Constants.MAIN_PREF, MODE_PRIVATE)
            preferences.edit().putString(Constants.FCM_TOKEN, token).apply()
            Log.d(TAG, "Fetching FCM registration token ${token}")
        })
    }

    private fun handleApiResponse() {
        viewModel.userDetailsList.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    binding.progressBar.root.visibility = View.GONE
                    dataHandler.data?.let { data ->
                        Toast.makeText(this@LoginActivity, data.otp.toString(), Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, OtpActivity::class.java)
                        intent.putExtra(Constants.USER_DATA, data)
                        intent.putExtra(
                            Constants.PHONE_NUMBER,
                            binding.etPhoneNumber.text.toString()
                        )
                        startActivity(intent)
                        finish()
                    }
                }
                is DataHandler.ERROR -> {
                    binding.progressBar.root.visibility = View.GONE
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> binding.progressBar.root.visibility = View.VISIBLE
            }

        }
    }

    override fun getViewBinding(): ActivityLoginBinding =
        ActivityLoginBinding.inflate(layoutInflater)
}