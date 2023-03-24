package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityInboxBinding

import com.ciaorides.ciaorides.view.activities.BaseActivity

class InboxActivity : BaseActivity<ActivityInboxBinding>() {
    override fun getViewBinding(): ActivityInboxBinding =
        ActivityInboxBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.inbox)
        binding.toolbar.profileView.visibility = View.GONE
    }
}