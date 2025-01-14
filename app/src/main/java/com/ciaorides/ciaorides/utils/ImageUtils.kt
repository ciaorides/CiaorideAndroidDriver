package com.ciaorides.ciaorides.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ciaorides.ciaorides.R

object ImageUtils {

    fun uploadCircularBitmap(context: Context, uri: Uri?, view: ImageView) {
        uri?.let {
            Glide.with(context).load(uri)
                .placeholder(R.drawable.ic_user)
                .apply(RequestOptions.circleCropTransform())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(view)
        }
    }
}