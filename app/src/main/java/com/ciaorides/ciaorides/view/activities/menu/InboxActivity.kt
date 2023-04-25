package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityInboxBinding

import com.ciaorides.ciaorides.view.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InboxActivity : BaseActivity<ActivityInboxBinding>() {
    override fun getViewBinding(): ActivityInboxBinding =
        ActivityInboxBinding.inflate(layoutInflater)

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge)
        binding.toolbar.tvHeader.text = getString(R.string.inbox)
//        binding.toolbar.profileView.visibility = View.GONE
        binding.noResultsFound.visibility =View.VISIBLE
        binding.rvInbox.visibility =View.GONE

        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
    }
}