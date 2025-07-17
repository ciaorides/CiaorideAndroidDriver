package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ItemPaymentsBinding
import com.ciaorides.ciaorides.databinding.ItemTransactionsBinding
import com.ciaorides.ciaorides.model.response.GetTransactionResposne
import javax.inject.Inject

class TransactionsAdapter @Inject constructor(private val listener : OnItemClickListener) :
    RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {
    private val diffUtil =
        object : DiffUtil.ItemCallback<GetTransactionResposne.Transactions>() {
            override fun areItemsTheSame(
                oldItem: GetTransactionResposne.Transactions,
                newItem: GetTransactionResposne.Transactions
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GetTransactionResposne.Transactions,
                newItem: GetTransactionResposne.Transactions
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    interface OnItemClickListener {
        fun onItemClick(position: Int, transaction: GetTransactionResposne.Transactions)
    }

    val onItemClickListener : OnItemClickListener = listener

    inner class ViewHolder(val binding: ItemTransactionsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTransactionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = differ.currentList[position]
        with(holder.binding) {
            ietmPaymentBookingId.text = "Transaction Type : ${transaction.transaction_type}"
            ietmPaymentAmount.text = "₹ " + transaction.amount.toString()
            ietmPaymentTime.text = transaction.created_date

            val color = if (transaction.transaction_type.equals("withdraw", ignoreCase = true) || transaction.transaction_type.equals("commission", ignoreCase = true)) {
                R.color.colorFullRed
            } else R.color.green
            ietmPaymentAmountLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, color))
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}