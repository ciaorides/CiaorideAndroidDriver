package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityAddBankBinding
import com.ciaorides.ciaorides.model.response.BankDetailsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.Constants.KEY_BANK_DETAILS
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBankActivity :  BaseActivity<ActivityAddBankBinding>() {

    private val viewModel: MenuViewModel by viewModels()
   lateinit var bankModel : BankDetailsResponse.Response

    override fun getViewBinding(): ActivityAddBankBinding =
        ActivityAddBankBinding.inflate(layoutInflater)

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge,binding.toolbar.ivProfileImage)
        binding.toolbar.tvHeader.text = getString(R.string.add_a_bank)
        binding.toolbar.profileView.visibility = View.GONE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        bankModel = intent?.getParcelableExtra<BankDetailsResponse.Response>(KEY_BANK_DETAILS)!!
        if (bankModel != null) {
            viewModel.bankId = bankModel.id!!
            viewModel.isEditBankDetails = true
            binding.tVNameOfBank.setText( bankModel.bank_name)
            binding.tVLocation.setText("")
            binding.tVAccountHolderName.setText( bankModel.account_holder_name)
            binding.tVAccountNumber.setText( bankModel.account_number)
            binding.tVIFSC.setText( bankModel.ifsc_code)
        }
        binding.BtnSaveDetails.setOnClickListener{
            viewModel.nameOfBank.value = binding.tVNameOfBank.text.toString()
            viewModel.location.value = binding.tVLocation.text.toString()
            viewModel.accountHolderName.value = binding.tVAccountHolderName.text.toString()
            viewModel.accountNumber.value = binding.tVAccountNumber.text.toString()
            viewModel.ifscCode.value = binding.tVIFSC.text.toString()
            viewModel.validateBankForm(Constants.getValue(this@AddBankActivity, Constants.USER_ID))
        }

        handleMyBankDetails()
        showErrorMessage()

    }

    private fun showErrorMessage() {
        viewModel.showErrorMessage.observe(this){it->
            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleMyBankDetails() {
        viewModel.saveBankResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            Toast.makeText(this@AddBankActivity, data.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this@AddBankActivity, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

}