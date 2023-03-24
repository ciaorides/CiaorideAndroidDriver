package com.ciaorides.ciaorides.view.activities.ui.vehicleDetails

import android.view.View
import androidx.activity.viewModels
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityVehicleDetailsBinding
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.AddVehiclesAdapterMain
import com.ciaorides.ciaorides.view.fragments.AddVehiclesStep1Fragment
import com.ciaorides.ciaorides.view.fragments.AddVehiclesStep2Fragment
import com.ciaorides.ciaorides.view.fragments.AddVehiclesStep3Fragment
import com.ciaorides.ciaorides.viewmodel.ManageVehicleImagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleDetailsActivity : BaseActivity<ActivityVehicleDetailsBinding>() {
    private val viewModel: ManageVehicleImagesViewModel by viewModels()

    override fun getViewBinding(): ActivityVehicleDetailsBinding =
        ActivityVehicleDetailsBinding.inflate(layoutInflater)

    private var addVehicleStep1Fragment: AddVehiclesStep1Fragment? = null
    private var addVehicleStep2Fragment: AddVehiclesStep2Fragment? = null
    private var addVehicleStep3Fragment: AddVehiclesStep3Fragment? = null

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.vehicle_details)
        binding.toolbar.profileView.visibility = View.GONE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        val data = ArrayList<String>()
        data.add(getString(R.string.book_a_taxi))
        data.add(getString(R.string.start_pooling))
        data.add(getString(R.string.travel_intercity))

        val viewPagerAdapter = AddVehiclesAdapterMain(supportFragmentManager)

        // add the fragments
        addVehicleStep1Fragment = AddVehiclesStep1Fragment.newInstance()
        addVehicleStep2Fragment = AddVehiclesStep2Fragment.newInstance()
        addVehicleStep3Fragment = AddVehiclesStep3Fragment.newInstance()
        viewPagerAdapter.add(addVehicleStep1Fragment)
        viewPagerAdapter.add(addVehicleStep2Fragment)
        viewPagerAdapter.add(addVehicleStep3Fragment)


        binding.pagerAddVehicle.adapter = viewPagerAdapter
        binding.pagerAddVehicle.beginFakeDrag()
        binding.btnSave.setOnClickListener {
            if (binding.pagerAddVehicle.currentItem == 0) {
                addVehicleStep1Fragment?.makeFirstStepCall()
            } else if (binding.pagerAddVehicle.currentItem == 1) {
                addVehicleStep2Fragment?.makeFirstStepCall()
            } else if (binding.pagerAddVehicle.currentItem == 2) {
                addVehicleStep3Fragment?.makeFirstStepCall()
            }

        }

        binding.txt1.setTextColor(resources.getColor(R.color.appGray))
        binding.txt2.setTextColor(resources.getColor(R.color.appGray))
        binding.txt3.setTextColor(resources.getColor(R.color.appGray))
        changeTabs(0)

    }


    fun changeTabs(index: Int) {
        when (index) {
            0 -> {
                binding.txt1.setTextColor(resources.getColor(R.color.white))
                binding.txt2.setTextColor(resources.getColor(R.color.appGray))
                binding.txt3.setTextColor(resources.getColor(R.color.appGray))
                binding.imgRound1.visibility = View.VISIBLE
                binding.imgRound1.setBackgroundResource(R.drawable.circle_blue)
                binding.imgRound2.visibility = View.GONE
                binding.imgRound3.visibility = View.GONE
            }
            1 -> {

                binding.txt1.setTextColor(resources.getColor(R.color.white))
                binding.txt2.setTextColor(resources.getColor(R.color.white))
                binding.txt3.setTextColor(resources.getColor(R.color.appGray))

                binding.imgRound1.setBackgroundResource(R.drawable.circle_green)
                binding.imgRound2.visibility = View.VISIBLE
                binding.imgRound2.setBackgroundResource(R.drawable.circle_blue)
                binding.imgRound3.visibility = View.GONE
                binding.pagerAddVehicle.setCurrentItem(index, true)

            }
            else -> {
                binding.txt1.setTextColor(resources.getColor(R.color.white))
                binding.txt2.setTextColor(resources.getColor(R.color.white))
                binding.txt3.setTextColor(resources.getColor(R.color.white))
                binding.imgRound1.setBackgroundResource(R.drawable.circle_green)
                binding.imgRound2.setBackgroundResource(R.drawable.circle_green)
                binding.imgRound3.visibility = View.VISIBLE
                binding.imgRound3.setBackgroundResource(R.drawable.circle_blue)
                binding.pagerAddVehicle.setCurrentItem(index, true)
            }
        }
    }
}