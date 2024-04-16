package com.ciaorides.ciaorides.view.activities.menu

import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityRidesBinding
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.SearchHistoryAdapter
import com.ciaorides.ciaorides.view.fragments.MyRidesFragment
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RidesActivity : BaseActivity<ActivityRidesBinding>() {
    override fun getViewBinding(): ActivityRidesBinding =
        ActivityRidesBinding.inflate(layoutInflater)

    private val viewModel: MenuViewModel by viewModels()
    override fun init() {
        handleMyRides()
        binding.toolbar.tvHeader.text = getString(R.string.my_rides)
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        if (!TextUtils.isEmpty(Constants.getValue(this@RidesActivity, Constants.USER_ID))) {
            binding.progressLayout.root.visibility = View.VISIBLE
            viewModel.getMyRides(
                GlobalUserIdRequest(
                    user_id = Constants.getValue(this@RidesActivity, Constants.USER_ID),
                    user_type = "user"
                )
            )
        }
    }

    private fun setUpTabLayout(
        response: MyRidesResponse,
    ) {
        response.let {
            binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = SearchHistoryAdapter(supportFragmentManager).apply {
                addFragment(
                    MyRidesFragment.newInstance(response, 0),
                    "Rides Taken"
                )
               /* addFragment(
                    MyRidesFragment.newInstance(response, 1),
                    "Rides Offering"
                )*/
                addFragment(
                    MyRidesFragment.newInstance(response, 2),
                    "Rides Scheduled"
                )
            }
            binding.viewPager.adapter = adapter
            binding.tabLayout.setupWithViewPager(binding.viewPager)
        }
    }

    private fun handleMyRides() {
        viewModel.myRidesResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            setUpTabLayout(data)
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {
                    // Do Nothing
                }
            }

        }
    }

}