package com.ciaorides.ciaorides.view.activities.menu


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityEarningsBinding
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.EarningsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.utils.openWhatsApp
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import me.ithebk.barchart.BarChartModel
import java.util.Random


@AndroidEntryPoint
class EarningsActivity : BaseActivity<ActivityEarningsBinding>() {

    private val viewModel: MenuViewModel by viewModels()
    override fun getViewBinding(): ActivityEarningsBinding =
        ActivityEarningsBinding.inflate(layoutInflater)

    var graphItemSelected = "week"
    override fun init() {
        updateToolBar(binding.toolbar.ivBadge,binding.toolbar.ivProfileImage)
        binding.toolbar.tvHeader.text = getString(R.string.title_activity_earnings)
        binding.toolbar.profileView.visibility = View.GONE

        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        initViews()
        handleMyEarnings()
    }

    private fun initViews() {
        binding.earningsPendingDetails.setOnClickListener {
            startActivity(Intent(this@EarningsActivity, PendingPaymentsActivity::class.java))
        }
        binding.earningsCompleteDetails.setOnClickListener{
            startActivity(Intent(this@EarningsActivity, CompletedPaymentsActivity::class.java))
        }

        binding.btnSupport.setOnClickListener {
            openWhatsApp(this, "+919441500416", "Write your query!")
        }

        val graphData = arrayOf(
            "This Week",
            "This Month",
            "This Year"
        )
        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            graphData
        )
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.graphSpinner.adapter = ad
        binding.graphSpinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        graphItemSelected = "week"
                    }
                    1 -> {
                        graphItemSelected = "month"
                    }
                    2 -> {
                        graphItemSelected = "year"
                    }
                    else -> {
                        // Open calendar
                    }
                }
                getMyEarnings()
            }
        }
    }

    private fun getMyEarnings() {
       // binding.progressLayout.root.visibility = View.VISIBLE
        viewModel.getMyEarnings(
            GlobalUserIdRequest(
                user_id = Constants.getValue(this@EarningsActivity, Constants.USER_ID),
                type = graphItemSelected
            )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun handleMyEarnings() {
        viewModel.earningsResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.response == null) {
                            binding.earningsScrollView.visibility = View.GONE
                            binding.noResultsFound.visibility = View.VISIBLE
                        } else {
                            binding.earningsScrollView.visibility = View.VISIBLE
                            binding.noResultsFound.visibility = View.GONE
//                            if (!data.response.resultdata2)
                              binding.textViewOnlineTime.setText(data.response.resultdata2.final_data.online)
                              binding.textViewTotalTrips.setText("${data.response.resultdata2.final_data.total_trips}")
                            binding.tvPrice.setText(data.total_amount)
                           /* adapter.differ.submitList(data.response)
                            binding.rvRides.apply {
                                adapter = adapter
                                layoutManager = LinearLayoutManager(this@EmergencyContact)
                                visibility = View.VISIBLE
                            }
                            binding.noResultsFound.visibility = View.GONE*/

                            binding.earningPendingDate.text = "Payment scheduled on ${data.response.resultdata2.payment_pending.completed_date}"
                            binding.earningPendingAmount.text = "₹ ${data.response.resultdata2.payment_pending.amount} /-"

                            binding.earningCompleteDate.text = "Last payment received on ${data.response.resultdata2.payment_completed.completed_date}"
                            binding.earningCompletedAmount.text = "₹ ${data.response.resultdata2.payment_completed.amount} /-"
                            setBarChart(data.payments)
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {

                }
            }

        }
    }

    private fun setBarChart(payments: List<EarningsResponse.PaymentGraphData>) {
        val dataColor = intArrayOf(
            Color.parseColor("#0F1899"),
            Color.parseColor("#0070F8")
        )
        val value = payments.maxByOrNull { it.total_amount.toDouble() }
        binding.barChart.barMaxValue = value!!.total_amount.toDouble().toInt()
        for (i in 0..(payments.size-1)) {
            val barChartModel = BarChartModel()
            barChartModel.barValue = payments[i].total_amount.toDouble().toInt()
            if (i%2 == 0){
                barChartModel.barColor = dataColor[0]
            } else {
                barChartModel.barColor = dataColor[1]
            }
            barChartModel.barTag = payments[i].day
            if (payments[i].total_amount.toDouble().toInt() == 0){
                if (payments.size == 7) {
                    when (i) {
                        0 -> barChartModel.barText = "S"
                        1 -> barChartModel.barText = "M"
                        2 -> barChartModel.barText = "T"
                        3 -> barChartModel.barText = "W"
                        4 -> barChartModel.barText = "T"
                        5 -> barChartModel.barText = "F"
                        6 -> barChartModel.barText = "S"
                    }
                } else if (payments.size == 12){
                    when (i) {
                        0 -> barChartModel.barText = "J"
                        1 -> barChartModel.barText = "F"
                        2 -> barChartModel.barText = "M"
                        3 -> barChartModel.barText = "A"
                        4 -> barChartModel.barText = "M"
                        5 -> barChartModel.barText = "J"
                        6 -> barChartModel.barText = "J"
                        7 -> barChartModel.barText = "A"
                        8 -> barChartModel.barText = "S"
                        9 -> barChartModel.barText = "O"
                        10 -> barChartModel.barText = "N"
                        11 -> barChartModel.barText = "D"
                    }
                } else {
                    barChartModel.barText = barChartModel.barTag as String
                }
            } else {
                barChartModel.barText = barChartModel.barValue.toString()
            }
            binding.barChart.addBar(barChartModel)
        }
    }

}
