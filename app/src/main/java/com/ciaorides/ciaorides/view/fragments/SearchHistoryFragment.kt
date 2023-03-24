package com.ciaorides.ciaorides.view.fragments

import android.os.Bundle
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.FragmentSearchHistoryListDialogBinding
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.view.adapter.SearchHistoryAdapter
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint


/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    SearchHistoryFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */

class SearchHistoryFragment {
    var setClickListener: ((user: RecentSearchesResponse.Response.UserLastData) -> Unit)? =
        null


    fun onRecentClicked(listener: (RecentSearchesResponse.Response.UserLastData) -> Unit) {
        setClickListener = listener
    }

     fun setUpTabLayout(
        binding: FragmentSearchHistoryListDialogBinding,
        response: RecentSearchesResponse,
        childFragmentManager: FragmentManager
    ) {
        response.let {
            val searchHistory = SearchHistoryItemFragment.newInstance(response, 0)
            val fevs = SearchHistoryItemFragment.newInstance(response, 1)
            searchHistory.onRecentClicked { recent ->
                setClickListener?.let {
                    it(recent)
                }
            }
            fevs.onRecentClicked { recent ->
                setClickListener?.let {
                    it(recent)
                }
            }
            val adapter = SearchHistoryAdapter(childFragmentManager).apply {
                addFragment(
                    searchHistory,
                    "Recent Searches"
                )
                addFragment(
                    fevs,
                    "Favourites"
                )
                addFragment(
                    SearchHistoryItemFragment.newInstance(response, 2),
                    "Scheduled"
                )
            }
            binding.viewPager.adapter = adapter
            binding.tabLayout.setupWithViewPager(binding.viewPager)
        }
    }
}