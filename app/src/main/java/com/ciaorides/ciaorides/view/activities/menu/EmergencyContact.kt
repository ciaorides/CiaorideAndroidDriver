package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityEmergencyContactBinding
import com.ciaorides.ciaorides.view.activities.BaseActivity

class EmergencyContact: BaseActivity<ActivityEmergencyContactBinding>() {
    override fun getViewBinding(): ActivityEmergencyContactBinding =
        ActivityEmergencyContactBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.emergency_contact)
        binding.toolbar.profileView.visibility = View.GONE
    }
}