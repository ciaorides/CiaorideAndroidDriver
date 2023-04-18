package com.ciaorides.ciaorides.view.activities.menu

import android.content.Intent
import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivitySettingsBinding
import com.ciaorides.ciaorides.view.activities.BaseActivity

class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {
    override fun getViewBinding(): ActivitySettingsBinding =
        ActivitySettingsBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.settings)
        binding.toolbar.profileView.visibility = View.GONE
        binding.layoutChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
        binding.layoutEmergencyContact.setOnClickListener {
            startActivity(Intent(this, EmergencyContact::class.java))
        }
        binding.layoutLogout.setOnClickListener {}
    }
}