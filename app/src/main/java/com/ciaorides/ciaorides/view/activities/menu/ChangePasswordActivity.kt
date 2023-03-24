package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityChangePasswordBinding
import com.ciaorides.ciaorides.view.activities.BaseActivity

class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding>() {
    override fun getViewBinding(): ActivityChangePasswordBinding =
        ActivityChangePasswordBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.settings)
        binding.toolbar.profileView.visibility = View.GONE
    }
}