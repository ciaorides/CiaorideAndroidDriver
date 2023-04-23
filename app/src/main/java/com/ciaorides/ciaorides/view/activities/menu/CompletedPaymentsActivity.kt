package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityCompletedPaymentsBinding
import com.ciaorides.ciaorides.view.activities.BaseActivity

class CompletedPaymentsActivity : BaseActivity<ActivityCompletedPaymentsBinding>() {
    override fun getViewBinding(): ActivityCompletedPaymentsBinding =
        ActivityCompletedPaymentsBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.completed_payments)
        binding.toolbar.profileView.visibility = View.GONE

        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
    }
}