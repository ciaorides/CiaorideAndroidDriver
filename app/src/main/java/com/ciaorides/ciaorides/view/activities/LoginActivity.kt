package com.ciaorides.ciaorides.view.activities

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ciaorides.ciaorides.databinding.ActivityLoginBinding
import com.ciaorides.ciaorides.model.request.LoginRequest
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val viewModel: LoginViewModel by viewModels()
    override fun init() {
        handleApiResponse()
        binding.btnVerify.setOnClickListener {
            binding.etPhoneNumber.text.toString().let { phoneNumber ->
                if (phoneNumber.length > 9) {
                    binding.progressBar.root.visibility = View.VISIBLE
                    viewModel.validateUser(LoginRequest(phoneNumber, "No"))
                }
            }
        }
        //val data= Constants.getUserInfo(applicationContext)
    }

    private fun handleApiResponse() {
        viewModel.userDetailsList.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    binding.progressBar.root.visibility = View.GONE
                    dataHandler.data?.let { data ->
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