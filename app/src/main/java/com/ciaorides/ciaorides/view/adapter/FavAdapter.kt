package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.databinding.ItemFavBinding
import com.ciaorides.ciaorides.databinding.ItemRecentSearchBinding
import com.ciaorides.ciaorides.model.response.FavResponse
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import javax.inject.Inject

class FavAdapter @Inject constructor() :
    RecyclerView.Adapter<FavAdapter.ViewHolder>() {


    private val diffUtil =
        object : DiffUtil.ItemCallback<FavResponse.Response>() {
            override fun areItemsTheSame(
                oldItem: FavResponse.Response,
                newItem: FavResponse.Response
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: FavResponse.Response,
                newItem: FavResponse.Response
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: ItemFavBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFavBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recent = differ.currentList[position]
        holder.binding.apply {
            tvName.text = recent.type
            tvLocation.text = recent.address
        }
        holder.binding.ivDelete.setOnClickListener {
            setClickListener?.let {
                it(recent)
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setClickListener: ((user: FavResponse.Response) -> Unit)? =
        null

    fun onClicked(listener: (FavResponse.Response) -> Unit) {
        setClickListener = listener
    }
}