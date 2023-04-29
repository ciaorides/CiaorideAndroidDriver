package com.ciaorides.ciaorides.view.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.FragmentPromotionBinding
import com.ciaorides.ciaorides.model.response.HomeBannersResponse
import com.ciaorides.ciaorides.utils.Constants

class PromotionFragment : Fragment(R.layout.fragment_promotion) {
    private lateinit var binding: FragmentPromotionBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPromotionBinding.bind(view)
        val data = arguments?.get(Constants.BANNER_RESP)
        if (data is HomeBannersResponse.Response.Top) {
            setBannerImg(data.banner_image)
        } else if (data is HomeBannersResponse.Response.Middle) {
            setBannerImg(data.banner_image)
        }
    }

    private fun setBannerImg(banner: String) {
        activity?.let {
            Constants.showGlide(it, banner, binding.ivPromotions, binding.progress)
        }
    }


    companion object {
        fun newInstance(
            bannerTopResp: HomeBannersResponse.Response.Top? = null,
            bannerMiddleResp: HomeBannersResponse.Response.Middle? = null
        ): PromotionFragment {
            val args = Bundle()
            if (bannerTopResp != null) {
                args.putParcelable(Constants.BANNER_RESP, bannerTopResp)
            } else {
                args.putParcelable(Constants.BANNER_RESP, bannerMiddleResp)
            }
            val fragment = PromotionFragment()
            fragment.arguments = args
            return fragment
        }
    }

}