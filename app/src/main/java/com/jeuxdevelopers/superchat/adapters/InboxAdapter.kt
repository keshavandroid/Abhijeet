package com.jeuxdevelopers.superchat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.databinding.ItemInboxBinding
import com.jeuxdevelopers.superchat.enums.MessageType
import com.jeuxdevelopers.superchat.models.InboxModel
import com.jeuxdevelopers.superchat.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class InboxAdapter(val data: MutableList<InboxModel>, val context: Context, val listener:InboxSelectListener) :
    RecyclerView.Adapter<InboxAdapter.InboxViewHolder>() {

    inner class InboxViewHolder(val binding: ItemInboxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: InboxModel) {
            Log.d("InboxAdapter", "bind: ")
            binding.tvUserName.text = model.senderName
            if (model.unReadCount > 0) {
                binding.tvMsgCount.visibility = View.VISIBLE
                binding.tvMsgCount.text = model.unReadCount.toString()
            } else {
                binding.tvMsgCount.visibility = View.GONE
            }
            if (model.messageType == MessageType.TEXT) {
                if (model.message.length > 12) {
                    binding.tvMessage.text = "${model.message.substring(0, 12)}..."
                } else {
                    binding.tvMessage.text = model.message
                }
            } else if (model.messageType == MessageType.IMAGE) {
                binding.tvMessage.text = MessageType.IMAGE.name
            }
            // time test----------------
            val format = SimpleDateFormat("dd MMM yy, hh:mm", Locale.getDefault())
            val utcMillis = Utils.localTimeToUtc(System.currentTimeMillis())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = utcMillis
            Log.d("Inbox-Time", "bind: UTC time ->${format.format(calendar.time)}")
            calendar.timeInMillis = Utils.utcTimeToLocal(utcMillis)
            Log.d("Inbox-Time", "bind: Local time ->${format.format(calendar.time)}")
            //-----------------------

            binding.tvTime.text = Utils.getTimeWithLast(Utils.utcTimeToLocal(model.time))
            binding.civProfile.setImageResource(R.drawable.hqdefault)

           // Glide.with(context).load(model.userImage).into(binding.civProfile)

            binding.root.setOnClickListener { listener.onInboxSelect(model) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder {
        return InboxViewHolder(
            ItemInboxBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: InboxViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface InboxSelectListener{
        fun onInboxSelect(model: InboxModel)
    }
}