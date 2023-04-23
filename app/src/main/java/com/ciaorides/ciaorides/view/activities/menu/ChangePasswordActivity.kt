package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityChangePasswordBinding
import com.ciaorides.ciaorides.model.request.ChangePassword
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.Constants.TEMP_USER_ID
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding>() {
    override fun getViewBinding(): ActivityChangePasswordBinding =
        ActivityChangePasswordBinding.inflate(layoutInflater)


    private val viewModel: MenuViewModel by viewModels()


    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.settings)
        binding.toolbar.profileView.visibility = View.GONE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }

        binding.BtnSave.setOnClickListener {
            if (binding.edtOldPassword.text.toString().isNullOrBlank()) {
                Toast.makeText(this, "Please enter Old Password", Toast.LENGTH_SHORT).show()
            } else if (binding.edtNewPassword.text.toString().isNullOrBlank()) {
                Toast.makeText(this, "Please enter New Password", Toast.LENGTH_SHORT).show()
            } else if (binding.edtConfirmPassword.text.toString().isNullOrBlank()) {
                Toast.makeText(this, "Please enter Confirm Password", Toast.LENGTH_SHORT).show()
            } else {
                val changePasswordReq = ChangePassword(
                    TEMP_USER_ID,
                    binding.edtOldPassword.text.toString(), binding.edtNewPassword.text.toString()
                )
                changePasswordCall(changePasswordReq)
            }

        }
        handleChangePassword()
    }

    private fun changePasswordCall(changePasswordReq: ChangePassword) {
//        if (!TextUtils.isEmpty(Constants.getValue(this@BankDetailsActivity, Constants.USER_ID))) {
        binding.progressLayout.root.visibility = View.VISIBLE
        viewModel.changePassword(changePasswordReq)
//        }
    }

    private fun handleChangePassword() {
        viewModel.changePasswordResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            Toast.makeText(applicationContext, data.message, Toast.LENGTH_SHORT)
                                .show()
                        }else
                        {
                            Toast.makeText(applicationContext, data.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


}