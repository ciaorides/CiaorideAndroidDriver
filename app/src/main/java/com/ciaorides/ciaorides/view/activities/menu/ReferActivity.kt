package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityInboxBinding
import com.ciaorides.ciaorides.databinding.ActivityReferBinding
import com.ciaorides.ciaorides.view.activities.BaseActivity

class ReferActivity : BaseActivity<ActivityReferBinding>() {
    override fun getViewBinding(): ActivityReferBinding =
        ActivityReferBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.refer_friend)
        binding.toolbar.profileView.visibility = View.GONE
    }
}