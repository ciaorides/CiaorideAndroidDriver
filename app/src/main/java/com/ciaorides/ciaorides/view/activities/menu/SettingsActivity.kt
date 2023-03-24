package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivitySettingsBinding
import com.ciaorides.ciaorides.view.activities.BaseActivity

class SettingsActivity: BaseActivity<ActivitySettingsBinding>() {
    override fun getViewBinding(): ActivitySettingsBinding =
        ActivitySettingsBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.settings)
        binding.toolbar.profileView.visibility = View.GONE
    }
}