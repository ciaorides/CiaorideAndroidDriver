package com.ciaorides.ciaorides.view.activities.menu


import android.opengl.Visibility
import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityPaymentsBinding
import com.ciaorides.ciaorides.view.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentsActivity : BaseActivity<ActivityPaymentsBinding>() {
    override fun getViewBinding(): ActivityPaymentsBinding =
        ActivityPaymentsBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.payments)
        binding.toolbar.profileView.visibility = View.GONE
    }
}