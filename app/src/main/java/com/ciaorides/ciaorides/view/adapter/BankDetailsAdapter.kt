package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.databinding.ItemBankDetailsBinding
import com.ciaorides.ciaorides.model.response.BankDetailsResponse
import javax.inject.Inject

class BankDetailsAdapter @Inject constructor() :
    RecyclerView.Adapter<BankDetailsAdapter.ViewHolder>() {
    private val diffUtil =
        object : DiffUtil.ItemCallback<BankDetailsResponse.Response>() {
            override fun areItemsTheSame(
                oldItem: BankDetailsResponse.Response,
                newItem: BankDetailsResponse.Response
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: BankDetailsResponse.Response,
                newItem: BankDetailsResponse.Response
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: ItemBankDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBankDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bankModel = differ.currentList[position]
        with(holder.binding) {
            tvBankName.text = bankModel.bank_name
            tvAccountNumber.text = bankModel.account_number
            btnDelete.setOnClickListener {
                setClickListener?.invoke(bankModel)
            }
            btnEdit.setOnClickListener{
                editClickListener?.invoke(bankModel)
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setClickListener: ((user: BankDetailsResponse.Response) -> Unit)? =
        null
    private var editClickListener: ((user: BankDetailsResponse.Response) -> Unit)? =
        null

    fun onDeleteClicked(listener: (BankDetailsResponse.Response) -> Unit) {
        setClickListener = listener

    }fun onEditClicked(listener: (BankDetailsResponse.Response) -> Unit) {
        editClickListener = listener
    }
}