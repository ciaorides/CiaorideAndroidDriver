package com.ciaorides.ciaorides.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ciaorides.ciaorides.model.response.HomeBannersResponse
import com.ciaorides.ciaorides.utils.PromotionTypes
import com.ciaorides.ciaorides.view.fragments.PromotionFragment

class PromotionsAdapter(
    fragmentManager: FragmentManager,
    private val promotionTypes: PromotionTypes,
    private val details: HomeBannersResponse.Response
) :
    FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        if (promotionTypes == PromotionTypes.TOP) {
            return PromotionFragment.newInstance(bannerTopResp = details.top[position])
        } else {
            return PromotionFragment.newInstance(bannerMiddleResp = details.middle[position])
        }
    }

    override fun getCount(): Int {
        if (promotionTypes == PromotionTypes.TOP) {
            return details.top.size
        } else {
            return details.middle.size
        }

    }
}