package com.ciaorides.ciaorides.view.activities.menu

import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityFavBinding
import com.ciaorides.ciaorides.model.request.DeleteBankDetailsRequest
import com.ciaorides.ciaorides.model.request.DeleteFavRequest
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.FavResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.Constants.TEMP_USER_ID
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.FavAdapter
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavActivity : BaseActivity<ActivityFavBinding>() {
    override fun getViewBinding(): ActivityFavBinding =
        ActivityFavBinding.inflate(layoutInflater)

    @Inject
    lateinit var favAdapter: FavAdapter

    private val viewModel: MenuViewModel by viewModels()
    override fun init() {
        handleFevs()
        binding.toolbar.tvHeader.text = getString(R.string.my_favourites)
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        binding.rvFev.apply {
            adapter = favAdapter
            layoutManager = LinearLayoutManager(this@FavActivity)
            visibility = View.VISIBLE
        }
        makeApiCall()
        handleDeleteFav()
        favAdapter.onClicked { vehicle ->
            Constants.showDeleteVehicleAlert(this@FavActivity) {
                if (it) {
                    binding.progressLayout.root.visibility = View.VISIBLE
                    deleteFav(vehicle.id)
                }
            }
        }
    }
    private fun makeApiCall(){
//        if (!TextUtils.isEmpty(Constants.getValue(this@FavActivity, Constants.USER_ID))) {
            binding.progressLayout.root.visibility = View.VISIBLE
            viewModel.getFav(
                GlobalUserIdRequest(
                    user_id = TEMP_USER_ID
                )
            )
//        }
    }

    private fun deleteFav(id: String) {
        viewModel.deleteFav(
            DeleteFavRequest(
                user_id = TEMP_USER_ID/*Constants.getValue(this@FavActivity, Constants.USER_ID)*/,
                favourite_id = id
            )
        )
    }

    private fun handleFevs() {
        viewModel.favResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            if (data.response.isEmpty()) {
                                binding.rvFev.visibility = View.GONE
                                binding.noResultsFound.visibility = View.VISIBLE
                            } else {
                                favAdapter.differ.submitList(data.response)
                                binding.rvFev.apply {
                                    adapter = favAdapter
                                    layoutManager = LinearLayoutManager(this@FavActivity)
                                    visibility = View.VISIBLE
                                }
                                binding.noResultsFound.visibility = View.GONE
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

    private fun handleDeleteFav() {
        viewModel.deleteVehicleResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            Toast.makeText(applicationContext, data.message, Toast.LENGTH_SHORT)
                                .show()
                            makeApiCall()
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