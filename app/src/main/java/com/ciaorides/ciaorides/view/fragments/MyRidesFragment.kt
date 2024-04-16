package com.ciaorides.ciaorides.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.FragmentMyRidesBinding
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.view.adapter.MyRidesAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyRidesFragment : Fragment(R.layout.fragment_my_rides) {
    @Inject
    lateinit var myRidesAdapter: MyRidesAdapter

    var setClickListener: ((user: RecentSearchesResponse.Response.UserLastData) -> Unit)? =
        null
    private lateinit var binding: FragmentMyRidesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyRidesBinding.bind(view)
        initData()
        myRidesAdapter.onClicked { recent ->
            setClickListener?.let {
                it(recent)
            }
        }
    }


    fun onRecentClicked(listener: (RecentSearchesResponse.Response.UserLastData) -> Unit) {
        setClickListener = listener
    }


    private fun initData() {
        val response = arguments?.getParcelable<MyRidesResponse>(Constants.DATA_VALUE)
        response?.let {
            if (arguments?.get(Constants.INDEX) == 0) {
                if (response.response.rides_taken != null && response.response.rides_taken.isEmpty()) {
                    binding.rvRides.visibility = View.GONE
                    binding.noResultsFound.visibility = View.VISIBLE
                } else {
                    myRidesAdapter.differ.submitList(response.response.rides_taken)
                    myRidesAdapter.typeRide = 0
                    binding.rvRides.apply {
                        adapter = myRidesAdapter
                        layoutManager = LinearLayoutManager(activity)
                        visibility = View.VISIBLE
                    }
                    binding.noResultsFound.visibility = View.GONE
                }


            } else if (arguments?.get(Constants.INDEX) == 1) {
                if (response.response.rides_offering!=null && response.response.rides_offering.isEmpty()) {
                    binding.rvRides.visibility = View.GONE
                    binding.noResultsFound.visibility = View.VISIBLE
                } else {
                    myRidesAdapter.differ.submitList(response.response.rides_offering)
                    myRidesAdapter.typeRide = 1
                    binding.rvRides.apply {
                        adapter = myRidesAdapter
                        layoutManager = LinearLayoutManager(activity)
                        visibility = View.VISIBLE
                    }
                    binding.noResultsFound.visibility = View.GONE
                }


            } else if (arguments?.get(Constants.INDEX) == 2) {
                if (response.response.rides_scheduled!= null && response.response.rides_scheduled.isEmpty()) {
                    binding.rvRides.visibility = View.GONE
                    binding.noResultsFound.visibility = View.VISIBLE
                } else {
                    myRidesAdapter.differ.submitList(response.response.rides_scheduled)
                    myRidesAdapter.typeRide = 2
                    binding.rvRides.apply {
                        adapter = myRidesAdapter
                        layoutManager = LinearLayoutManager(activity)
                        visibility = View.VISIBLE
                    }
                    binding.noResultsFound.visibility = View.GONE
                }

            }
        }
    }

    companion object {
        fun newInstance(response: MyRidesResponse, index: Int): MyRidesFragment {
            val args = Bundle()
            args.putInt(Constants.INDEX, index)
            args.putParcelable(Constants.DATA_VALUE, response)
            val fragment = MyRidesFragment()
            fragment.arguments = args
            return fragment
        }
    }

}