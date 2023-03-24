package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ciaorides.ciaorides.databinding.ItemPromotionalsBinding
import com.ciaorides.ciaorides.databinding.ItemPromotionsBottomBinding
import com.ciaorides.ciaorides.model.response.HomeBannersResponse
import com.ciaorides.ciaorides.utils.Constants
import javax.inject.Inject

class HomePromotionalAdapter @Inject constructor() :
    RecyclerView.Adapter<HomePromotionalAdapter.ViewHolder>() {


    private val diffUtil =
        object : DiffUtil.ItemCallback<HomeBannersResponse.Response.Bottom>() {
            override fun areItemsTheSame(
                oldItem: HomeBannersResponse.Response.Bottom,
                newItem: HomeBannersResponse.Response.Bottom
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: HomeBannersResponse.Response.Bottom,
                newItem: HomeBannersResponse.Response.Bottom
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 1) {
            val binding =
                ItemPromotionsBottomBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ViewHolder(binding)
        } else {
            val binding =
                ItemPromotionalsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val promotions = differ.currentList[position]
        if (holder.binding is ItemPromotionalsBinding) {
            holder.binding.apply {
                Constants.showGlide(
                    ivPromotions.context,
                    promotions.banner_image,
                    ivPromotions,
                    progress
                )
            }
        }

        holder.itemView.setOnClickListener {
            setArticleClickListener?.let {
                if (holder.binding is ItemPromotionalsBinding) {
                    it(promotions)
                }

            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setArticleClickListener: ((user: HomeBannersResponse.Response.Bottom) -> Unit)? =
        null

    fun onClicked(listener: (HomeBannersResponse.Response.Bottom) -> Unit) {
        setArticleClickListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        if (position == differ.currentList.size - 1) {
            return 1
        } else {
            return 0
        }
    }
}