package com.jeuxdevelopers.superchat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jeuxdevelopers.superchat.databinding.ItemHomeUserBinding
import com.jeuxdevelopers.superchat.models.UserModel

import java.util.ArrayList
import android.text.Spannable


import android.text.style.ForegroundColorSpan
import com.jeuxdevelopers.superchat.R
import java.util.regex.Matcher
import java.util.regex.Pattern


class UsersAdapter(
    private var data: MutableList<UserModel>,
    val context: Context,
    val listener: UserSelectListener
) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>(), Filterable {

    private var filteredData = data
    private var filterQuery = ""


    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:MutableList<UserModel>){
        data = list
        filteredData = data
        filterQuery = ""
        notifyDataSetChanged()
    }

    inner class UserViewHolder(val binding: ItemHomeUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(model: UserModel) {
            Glide.with(context).load(model.profileUrl)
//            addListener(object: RequestListener<Drawable>{
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    binding.pbLoading.visibility = View.INVISIBLE
//                    return true
//                }
//
//                override fun onResourceReady(
//                    resource: Drawable?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    binding.pbLoading.visibility = View.INVISIBLE
//                    return true
//                }
//
//            })
                .into(binding.civProfile)
            if (filterQuery.isEmpty()) {
                binding.tvUserName.text = model.name
            } else {
                setHighlightedName(model.name)
            }

            binding.tvAge.text = "Age: ${model.age}Yr"

            binding.root.setOnClickListener { listener.onUserSelect(model) }

        }

        private fun setHighlightedName(senderName: String) {
            val spanText = Spannable.Factory.getInstance().newSpannable(senderName.lowercase())
            val matcher: Matcher = Pattern.compile(filterQuery.lowercase())
                .matcher(senderName.lowercase())
            while (matcher.find()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    spanText.setSpan(
                        ForegroundColorSpan(context.resources.getColor(R.color.colorBlue, null)),
                        matcher.start(),
                        matcher.start() + filterQuery.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    spanText.setSpan(
                        ForegroundColorSpan(context.resources.getColor(R.color.colorBlue)),
                        matcher.start(),
                        matcher.start() + filterQuery.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            binding.tvUserName.text = spanText
        }

    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val query = constraint.toString().lowercase()
                filteredData = if (query.isEmpty()) {
                    data
                } else {
                    val filteredList: MutableList<UserModel> = ArrayList()
                    for (model in data) {
                        if (model.name.lowercase().contains(query)) {
                            filteredList.add(model)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredData
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredData = results.values as MutableList<UserModel>
                filterQuery = constraint.toString()
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            ItemHomeUserBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(filteredData[position])
    }

    override fun getItemCount(): Int {
        return filteredData.size
    }

    interface UserSelectListener {
        fun onUserSelect(userModel: UserModel)
    }
}