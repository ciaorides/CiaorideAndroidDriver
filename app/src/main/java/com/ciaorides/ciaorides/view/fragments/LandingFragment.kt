package com.ciaorides.ciaorides.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.FragmentLandingBinding
import com.ciaorides.ciaorides.utils.Constants

class LandingFragment : Fragment(R.layout.fragment_landing) {
    private lateinit var binding: FragmentLandingBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLandingBinding.bind(view)
        binding.tvTitle.text = arguments?.get(Constants.TITLE).toString()
        when {
            arguments?.get(Constants.INDEX) == 0 -> {
                binding.animationView.setAnimation(R.raw.car)
            }
            arguments?.get(Constants.INDEX) == 1 -> {
                binding.animationView.setAnimation(R.raw.car_2)
            }
            else -> {
                binding.animationView.setAnimation(R.raw.car_3)
            }
        }

    }


    companion object {
        fun newInstance(title: String, index: Int): LandingFragment {
            val args = Bundle()
            args.putString(Constants.TITLE, title)
            args.putInt(Constants.INDEX, index)
            val fragment = LandingFragment()
            fragment.arguments = args
            return fragment
        }
    }

}