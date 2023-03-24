package com.ciaorides.ciaorides.view.activities.menu


import android.view.View

import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityEarningsBinding
import com.ciaorides.ciaorides.view.activities.BaseActivity

class EarningsActivity : BaseActivity<ActivityEarningsBinding>() {
    override fun getViewBinding(): ActivityEarningsBinding =
        ActivityEarningsBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.payments)
        binding.toolbar.profileView.visibility = View.GONE
    }


  }