package com.ciaorides.ciaorides.view.activities.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityAddBankBinding
import com.ciaorides.ciaorides.databinding.ActivitySettingsBinding
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBankActivity :  BaseActivity<ActivityAddBankBinding>() {

    private val viewModel: MenuViewModel by viewModels()
    override fun getViewBinding(): ActivityAddBankBinding =
        ActivityAddBankBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.add_a_bank)
        binding.toolbar.profileView.visibility = View.GONE

        binding.BtnSaveDetails.setOnClickListener{
            viewModel.nameOfBank.value = binding.tVNameOfBank.text.toString()
            viewModel.location.value = binding.tVLocation.text.toString()
            viewModel.accountHolderName.value = binding.tVAccountHolderName.text.toString()
            viewModel.accountNumber.value = binding.tVAccountNumber.text.toString()
            viewModel.ifscCode.value = binding.tVIFSC.text.toString()
            viewModel.validateBankForm()
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
                            Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT).show()
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