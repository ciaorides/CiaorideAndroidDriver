package com.ciaorides.ciaorides.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.FragmentSearchHistoryItemBinding
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.view.adapter.RecentSchedulesAdapter
import com.ciaorides.ciaorides.view.adapter.RecentSearchAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchHistoryItemFragment : Fragment(R.layout.fragment_search_history_item) {
    @Inject
    lateinit var recentSearchAdapter: RecentSearchAdapter

    @Inject
    lateinit var recentSchedulesAdapter: RecentSchedulesAdapter

    var setClickListener: ((user: RecentSearchesResponse.Response.UserLastData) -> Unit)? =
        null
    private lateinit var binding: FragmentSearchHistoryItemBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchHistoryItemBinding.bind(view)
        initData()
        recentSearchAdapter.onClicked { recent ->
            setClickListener?.let {
                it(recent)
            }
        }
    }


    fun onRecentClicked(listener: (RecentSearchesResponse.Response.UserLastData) -> Unit) {
        setClickListener = listener
    }


    private fun initData() {
        val response = arguments?.getParcelable<RecentSearchesResponse>(Constants.DATA_VALUE)
        response?.let {
            if (arguments?.get(Constants.INDEX) == 0) {
                if (response.response.recent.isEmpty()) {
                    binding.rvRecent.visibility = View.GONE
                    binding.noResultsFound.visibility = View.VISIBLE
                } else {
                    recentSearchAdapter.differ.submitList(response.response.recent)
                    binding.rvRecent.apply {
                        adapter = recentSearchAdapter
                        layoutManager = LinearLayoutManager(activity)
                        visibility = View.VISIBLE
                    }
                    binding.noResultsFound.visibility = View.GONE
                }


            } else if (arguments?.get(Constants.INDEX) == 1) {
                if (response.response.favorite.isEmpty()) {
                    binding.rvRecent.visibility = View.GONE
                    binding.noResultsFound.visibility = View.VISIBLE
                } else {
                    recentSearchAdapter.differ.submitList(response.response.favorite)
                    binding.rvRecent.apply {
                        adapter = recentSearchAdapter
                        layoutManager = LinearLayoutManager(activity)
                        visibility = View.VISIBLE
                    }
                    binding.noResultsFound.visibility = View.GONE
                }


            } else {
                if (response.response.schedule.isEmpty()) {
                    binding.rvRecent.visibility = View.GONE
                    binding.noResultsFound.visibility = View.VISIBLE
                } else {
                    recentSchedulesAdapter.differ.submitList(response.response.schedule)
                    binding.rvRecent.apply {
                        adapter = recentSchedulesAdapter
                        layoutManager = LinearLayoutManager(activity)
                        visibility = View.VISIBLE
                    }
                    binding.noResultsFound.visibility = View.GONE
                }

            }
        }
    }

    companion object {
        fun newInstance(response: RecentSearchesResponse, index: Int): SearchHistoryItemFragment {
            val args = Bundle()
            args.putInt(Constants.INDEX, index)
            args.putParcelable(Constants.DATA_VALUE, response)
            val fragment = SearchHistoryItemFragment()
            fragment.arguments = args
            return fragment
        }
    }

}