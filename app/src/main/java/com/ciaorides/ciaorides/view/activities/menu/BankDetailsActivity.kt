package com.ciaorides.ciaorides.view.activities.menu

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityBankDetailsBinding
import com.ciaorides.ciaorides.model.request.DeleteBankDetailsRequest
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.Constants.KEY_BANK_DETAILS
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.BankDetailsAdapter
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BankDetailsActivity : BaseActivity<ActivityBankDetailsBinding>() {
    override fun getViewBinding(): ActivityBankDetailsBinding =
        ActivityBankDetailsBinding.inflate(layoutInflater)

    private val viewModel: MenuViewModel by viewModels()

    @Inject
    lateinit var bankDetailsAdapter: BankDetailsAdapter

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.bank_details)
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        handleMyBankDetails()
        handleDeleteBankDetails()
        bankDetailsCall()
        binding.addBank.setOnClickListener{
            startActivity(Intent(this,AddBankActivity::class.java))
        }
        bankDetailsAdapter.onDeleteClicked { bankModel ->
            Constants.showDeleteVehicleAlert(this@BankDetailsActivity) {
                if (it) {
                    binding.progressLayout.root.visibility = View.VISIBLE
                    bankModel.id?.let { it1 -> deleteBankDetails(it1) }
                }
            }
        }
        bankDetailsAdapter.onEditClicked { bankModel ->
            val intent =  Intent(this,AddBankActivity::class.java)
            intent.putExtra(KEY_BANK_DETAILS,bankModel)
            startActivity(intent)
        }
    }

    private fun bankDetailsCall() {
//        if (!TextUtils.isEmpty(Constants.getValue(this@BankDetailsActivity, Constants.USER_ID))) {
            binding.progressLayout.root.visibility = View.VISIBLE
            viewModel.getBankDetails(
                GlobalUserIdRequest(
                    user_id = Constants.getValue(this@BankDetailsActivity, Constants.USER_ID)
                )
            )
//        }
    }

    private fun deleteBankDetails(id: String) {
        viewModel.deleteBankDetails(
            DeleteBankDetailsRequest(
                user_id =Constants.getValue(this@BankDetailsActivity, Constants.USER_ID),
                bank_id = id
            )
        )
    }

    private fun handleDeleteBankDetails() {
        viewModel.deleteVehicleResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            Toast.makeText(applicationContext, data.message, Toast.LENGTH_SHORT)
                                .show()
                            bankDetailsCall()
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

    private fun handleMyBankDetails() {
        viewModel.bankDetailsResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            bankDetailsAdapter.differ.submitList(data.response)
                            binding.rvBank.apply {
                                adapter = bankDetailsAdapter
                                layoutManager = LinearLayoutManager(this@BankDetailsActivity)
                                visibility = View.VISIBLE
                            }
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