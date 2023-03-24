package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.databinding.ItemMessageSendBinding
import com.ciaorides.ciaorides.model.response.Message
import javax.inject.Inject

class MessageAdapter(var messageList : ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMessageSendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){
            txtMessage.text = messageList.get(position).text
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class ViewHolder(val binding: ItemMessageSendBinding) : RecyclerView.ViewHolder(binding.root)
}


