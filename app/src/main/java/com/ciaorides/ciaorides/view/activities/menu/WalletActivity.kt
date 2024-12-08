package com.ciaorides.ciaorides.view.activities.menu

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityWalletBinding
import com.ciaorides.ciaorides.model.request.AddTransactionRequest
import com.ciaorides.ciaorides.model.request.GetTransactionRequest
import com.ciaorides.ciaorides.model.request.WithdrawTransactionRequest
import com.ciaorides.ciaorides.model.response.GetTransactionResposne
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.utils.SweetAlertDialog
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.TransactionsAdapter
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.roundToInt

@AndroidEntryPoint
class WalletActivity : BaseActivity<ActivityWalletBinding>(), PaymentResultWithDataListener {
    private val viewModel: MenuViewModel by viewModels()
    lateinit var mTransactionsAdapter: TransactionsAdapter

    override fun init() {
        initViews()
        getTransactionsData()
        handleGetTransactions()
        handleAddTransaction()
        handleWithdrawTransaction()
        handleRazorPayResponse()
    }

    private fun handleRazorPayResponse() {
        viewModel.razorPayResponse.observe(this){dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            payAmountOnline(data.response.razor_key, binding.etWalletAmount.text.toString())
                        } else {
                            Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {

                }
            }
        }
    }

    private fun handleWithdrawTransaction() {
        viewModel.withdrawTransactionResponse.observe(this){ dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        Log.d("Wallet Withdraw", data.message)
                        /*Toast.makeText(this, data.message, Toast.LENGTH_SHORT)
                            .show()*/
                        hideKeyboard(this)
                        binding.etWalletAmount.setText("")
                        binding.etWalletAmount.clearFocus()
                        getTransactionsData()
                        Constants.showSweetAlert(this, SweetAlertDialog.SUCCESS_TYPE, "Success", "Payment withdrawal has initiated!", "OK")
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {

                }
            }

        }
    }

    private fun handleAddTransaction() {
        viewModel.addTransactionResponse.observe(this){ dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        Toast.makeText(applicationContext, data.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    hideKeyboard(this)
                    binding.etWalletAmount.setText("")
                    binding.etWalletAmount.clearFocus()
                    getTransactionsData()
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {

                }
            }

        }
    }

    private fun getTransactionsData() {
        binding.progressLayout.root.visibility = View.VISIBLE
        viewModel.getTransactions(
            GetTransactionRequest(
//                rider_id = "2250"
                rider_id = Constants.getValue(this@WalletActivity, Constants.USER_ID)
            )
        )
    }

    private fun handleGetTransactions() {
        viewModel.getTransactionResponse.observe(this){ dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.transactions == null) {
                            binding.rcvWalletList.visibility = View.GONE
                            binding.tvNoTranscations.visibility = View.VISIBLE
                        } else {
                            binding.rcvWalletList.visibility = View.VISIBLE
                            binding.tvNoTranscations.visibility = View.GONE
                            binding.tvWalletAmount.setText(data.total_amount.toString())
                            mTransactionsAdapter.differ.submitList(data.transactions)
                            binding.rcvWalletList.apply {
                                this.adapter = mTransactionsAdapter
                                this.layoutManager = LinearLayoutManager(this@WalletActivity, LinearLayoutManager.VERTICAL, false)
                                this.hasFixedSize()
                                this.visibility = View.VISIBLE
                                this.adapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {

                }
            }

        }
    }

    private fun initViews() {
        updateToolBar(binding.toolbar.ivBadge,binding.toolbar.ivProfileImage)
        binding.toolbar.tvHeader.text = getString(R.string.my_wallet)
        binding.toolbar.profileView.visibility = View.VISIBLE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }

        binding.btnAddMoney.setOnClickListener {
            if (binding.etWalletAmount.text.toString().isNotEmpty() && binding.etWalletAmount.text.toString().isDigitsOnly()){
                binding.progressLayout.root.visibility = View.VISIBLE
                viewModel.getRazorPayResponse()
            } else {
                Toast.makeText(this, "Please enter amount!", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnWithdraw.setOnClickListener {
            if (binding.etWalletAmount.text.toString().isNotEmpty() && binding.etWalletAmount.text.toString().isDigitsOnly()){
                binding.progressLayout.root.visibility = View.VISIBLE
                viewModel.postWithdrawTransactions(
                    WithdrawTransactionRequest(
                        rider_id = Constants.getValue(this@WalletActivity, Constants.USER_ID),
                        amount = binding.etWalletAmount.text.toString()
                    )
                )
            } else {
                Toast.makeText(this, "Please enter amount!", Toast.LENGTH_LONG).show()
            }
        }

        mTransactionsAdapter = TransactionsAdapter(object : TransactionsAdapter.OnItemClickListener {
            override fun onItemClick(
                position: Int,
                transation: GetTransactionResposne.Transactions
            ) {
                // Do Nothing now
            }

        })
    }

    private fun payAmountOnline(key:String, amountText : String) {
        val checkout = Checkout()
        Checkout.preload(this)
        checkout.setKeyID(key)
        checkout.setImage(R.drawable.app_icon);
        val amount = (amountText.toFloat() * 100).roundToInt().toInt()
        val requestData = JSONObject()
        try {
            // to put name
            requestData.put("name", getString(R.string.app_name))

            // put description
            requestData.put("description", "Test payment")

            // to set theme color
            //requestData.put("theme.color", "")

            // put the currency
            requestData.put("currency", "INR")

            // put amount
            requestData.put("amount", amount)

            // put mobile number
            requestData.put("prefill.contact", Constants.getValue(this, Constants.PHONE_NUMBER))

            // put email
            // requestData.put("prefill.email", "chaitanyamunje@gmail.com")

            // open razorpay to checkout activity
            checkout.open(this@WalletActivity, requestData)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getViewBinding(): ActivityWalletBinding = ActivityWalletBinding.inflate(layoutInflater)

    override fun onPaymentSuccess(razorpayPaymentID: String?, paymentData: PaymentData?) {
        try {
            Log.d("WalletActivity", paymentData!!.paymentId)
            Toast.makeText(this, razorpayPaymentID, Toast.LENGTH_LONG).show()
            binding.progressLayout.root.visibility = View.VISIBLE
            viewModel.postAddTransactions(
                AddTransactionRequest(
                    rider_id = Constants.getValue(this@WalletActivity, Constants.USER_ID),
                    amount = binding.etWalletAmount.text.toString(),
                    status = 1,
                    transaction_id = razorpayPaymentID!!
                )
            )
        } catch (e: Exception){
            println(e.printStackTrace())
        }
    }

    override fun onPaymentError(p0: Int, code: String?, response: PaymentData?) {
        try {
            Toast.makeText(this, code, Toast.LENGTH_LONG).show()
        } catch (e: Exception){
            println(e.printStackTrace())
        }
    }

}