package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.databinding.LayoutSideMenuBinding
import com.ciaorides.ciaorides.utils.Constants
import javax.inject.Inject

class MenuListAdapter @Inject constructor() : RecyclerView.Adapter<MenuListAdapter.ViewHolder>() {
    private val menusList = getMenuList()

    inner class ViewHolder(val binding: LayoutSideMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutSideMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvName.text = menusList.get(position)

        }
        holder.itemView.setOnClickListener {
            setItemClicked?.let {
                it(menusList.get(position))
            }
        }

    }

    override fun getItemCount(): Int {
        return menusList.size
    }

    private var setItemClicked: ((index: String) -> Unit)? = null

    fun MenuItemClicked(listener: (String) -> Unit) {
        setItemClicked = listener
    }

    private fun getMenuList(): ArrayList<String> {
        val data = ArrayList<String>()
        data.add(Constants.MENU_MY_RIDES)
        data.add(Constants.MENU_MY_EARNINGS)
        data.add(Constants.MENU_MY_VEHICLES)
        data.add(Constants.MENU_MY_FAVOURITES)
        data.add(Constants.MENU_BANK_DETAILS)
        data.add(Constants.MENU_INBOX)
        data.add(Constants.MENU_REFER_FRIEND)
        data.add(Constants.MENU_PAYMENTS)
        data.add(Constants.MENU_SETTINGS)
        data.add(Constants.MENU_ABOUT_US)
        data.add(Constants.MENU_TERMS_N_CONDITIONS)
        data.add(Constants.MENU_PRIVACY_POLICY)
        data.add(Constants.MENU_HELP)
        return data
    }
}