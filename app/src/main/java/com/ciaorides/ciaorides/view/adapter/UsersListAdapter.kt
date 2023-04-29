package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.databinding.LayoutSideMenuBinding
import com.ciaorides.ciaorides.model.UserDetailsItem
import javax.inject.Inject

class UsersListAdapter @Inject constructor() : RecyclerView.Adapter<UsersListAdapter.ViewHolder>() {


    private val diffUtil = object : DiffUtil.ItemCallback<UserDetailsItem>() {
        override fun areItemsTheSame(oldItem: UserDetailsItem, newItem: UserDetailsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UserDetailsItem,
            newItem: UserDetailsItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: LayoutSideMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutSideMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = differ.currentList[position]
        holder.binding.apply {
            tvName.text = user.name

        }
        holder.itemView.setOnClickListener {
            setArticleClickListener?.let {
                it(user)
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setArticleClickListener: ((user: UserDetailsItem) -> Unit)? = null

    fun onArticleClicked(listener: (UserDetailsItem) -> Unit) {
        setArticleClickListener = listener
    }
}