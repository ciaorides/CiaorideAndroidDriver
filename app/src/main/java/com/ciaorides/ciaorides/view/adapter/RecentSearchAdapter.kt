package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.databinding.ItemRecentSearchBinding
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import javax.inject.Inject

class RecentSearchAdapter @Inject constructor() :
    RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {


    private val diffUtil =
        object : DiffUtil.ItemCallback<RecentSearchesResponse.Response.UserLastData>() {
            override fun areItemsTheSame(
                oldItem: RecentSearchesResponse.Response.UserLastData,
                newItem: RecentSearchesResponse.Response.UserLastData
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: RecentSearchesResponse.Response.UserLastData,
                newItem: RecentSearchesResponse.Response.UserLastData
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: ItemRecentSearchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recent = differ.currentList[position]
        holder.binding.apply {
            tvTitle.text = recent.address
        }
        holder.binding.root.setOnClickListener {
            setClickListener?.let {
                it(recent)
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setClickListener: ((user: RecentSearchesResponse.Response.UserLastData) -> Unit)? =
        null

    fun onClicked(listener: (RecentSearchesResponse.Response.UserLastData) -> Unit) {
        setClickListener = listener
    }
}