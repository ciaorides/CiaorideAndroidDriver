package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityRidesBinding
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RidesActivity : BaseActivity<ActivityRidesBinding>() {
    override fun getViewBinding(): ActivityRidesBinding =
        ActivityRidesBinding.inflate(layoutInflater)

    private val viewModel: MenuViewModel by viewModels()
    override fun init() {
        // handleMyRides()
        binding.toolbar.tvHeader.text = getString(R.string.my_rides)
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }


    }

    private fun setUpTabLayout(
        response: MyRidesResponse,
    ) {
        response.let {

        }
    }

    private fun handleMyRides() {
        viewModel.myRidesResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {

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