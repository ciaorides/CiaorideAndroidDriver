package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.databinding.FragmentSchedulesBinding
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import javax.inject.Inject

class RecentSchedulesAdapter @Inject constructor() :
    RecyclerView.Adapter<RecentSchedulesAdapter.ViewHolder>() {


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

    inner class ViewHolder(val binding: FragmentSchedulesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FragmentSchedulesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scheduled = differ.currentList[position]
        holder.binding.apply {
            tvSchedules.text = scheduled.address
            tvTime.text = scheduled.created_on
        }
        holder.itemView.setOnClickListener {
            setArticleClickListener?.let {
                it(scheduled)
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setArticleClickListener: ((user: RecentSearchesResponse.Response.UserLastData) -> Unit)? =
        null

    fun onArticleClicked(listener: (RecentSearchesResponse.Response.UserLastData) -> Unit) {
        setArticleClickListener = listener
    }
}