package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.databinding.ItemEmergencyBinding
import com.ciaorides.ciaorides.model.response.BankDetailsResponse
import com.ciaorides.ciaorides.model.response.EmergencyContactResponse
import javax.inject.Inject

class EmergencyContactAdapter @Inject constructor() :
    RecyclerView.Adapter<EmergencyContactAdapter.ViewHolder>() {
    private val diffUtil =
        object : DiffUtil.ItemCallback<EmergencyContactResponse.Response>() {
            override fun areItemsTheSame(
                oldItem: EmergencyContactResponse.Response,
                newItem: EmergencyContactResponse.Response
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: EmergencyContactResponse.Response,
                newItem: EmergencyContactResponse.Response
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: ItemEmergencyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemEmergencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = differ.currentList[position]
        with(holder.binding) {
            txtContact.text = contact.mobile
            txtName.text = contact.name
            btnDelete.setOnClickListener {
                setClickListener?.invoke(contact)
            }
            btnEdit.setOnClickListener {
                editClickListener?.invoke(contact)
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setClickListener: ((user: EmergencyContactResponse.Response) -> Unit)? =
        null

    private var editClickListener: ((user: EmergencyContactResponse.Response) -> Unit)? =
        null

    fun onDeleteClicked(listener: (EmergencyContactResponse.Response) -> Unit) {
        setClickListener = listener
    }

    fun onEditClicked(listener: (EmergencyContactResponse.Response) -> Unit) {
        editClickListener = listener
    }
}