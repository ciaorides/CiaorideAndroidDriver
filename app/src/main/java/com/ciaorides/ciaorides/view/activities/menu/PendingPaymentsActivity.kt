package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityPendingPaymentsBinding
import com.ciaorides.ciaorides.view.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingPaymentsActivity : BaseActivity<ActivityPendingPaymentsBinding>() {
    override fun getViewBinding(): ActivityPendingPaymentsBinding =
        ActivityPendingPaymentsBinding.inflate(layoutInflater)

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge)
        binding.toolbar.tvHeader.text = getString(R.string.pending_payments)
        binding.toolbar.profileView.visibility = View.GONE

        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
    }

}