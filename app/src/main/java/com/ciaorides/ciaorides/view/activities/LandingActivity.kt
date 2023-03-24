package com.ciaorides.ciaorides.view.activities

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityLandingBinding
import com.ciaorides.ciaorides.view.adapter.LandingAdapter

class LandingActivity : BaseActivity<ActivityLandingBinding>() {
    override fun init() {
        val data = ArrayList<String>()
        data.add(getString(R.string.book_a_taxi))
        data.add(getString(R.string.start_pooling))
        data.add(getString(R.string.travel_intercity))
        binding.pagerIntros.adapter = LandingAdapter(
            supportFragmentManager,
            data
        )
        binding.btnNext.setOnClickListener {
            if (binding.pagerIntros.currentItem < 2) {
                binding.pagerIntros.setCurrentItem(binding.pagerIntros.currentItem + 1, true)
                changeTabs(binding.pagerIntros.currentItem)
            } else {
                navigateToLogin()
            }
        }
        binding.btnSkip.setOnClickListener {
            navigateToLogin()
        }
        binding.pagerIntros.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                changeTabs(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }

    private fun navigateToLogin() {
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }


    override fun getViewBinding(): ActivityLandingBinding =
        ActivityLandingBinding.inflate(layoutInflater)

    private fun changeTabs(index: Int) {
        when (index) {
            0 -> {
                binding.divider1.background =
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.divider_background_corner
                    )
                binding.divider2.background =
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.divider_background_corner_gray
                    )
                binding.divider3.background =
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.divider_background_corner_gray
                    )
                binding.btnNext.text = getString(R.string.next)
                binding.btnSkip.visibility = View.VISIBLE
            }
            1 -> {
                binding.divider1.background =
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.divider_background_corner_gray
                    )
                binding.divider2.background =
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.divider_background_corner
                    )
                binding.divider3.background =
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.divider_background_corner_gray
                    )
                binding.btnNext.text = getString(R.string.next)
                binding.btnSkip.visibility = View.VISIBLE
            }
            else -> {
                binding.divider1.background =
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.divider_background_corner_gray
                    )
                binding.divider2.background =
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.divider_background_corner_gray
                    )
                binding.divider3.background =
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.divider_background_corner
                    )
                binding.btnNext.text = getString(R.string.lets_go)
                binding.btnSkip.visibility = View.INVISIBLE
            }
        }
    }
}