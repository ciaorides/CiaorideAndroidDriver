package com.ciaorides.ciaorides.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ciaorides.ciaorides.view.fragments.LandingFragment

class LandingAdapter(fragmentManager: FragmentManager, private val details: ArrayList<String>) :
    FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return LandingFragment.newInstance(details[position],position)
    }

    override fun getCount(): Int {
        return details.size
    }
}