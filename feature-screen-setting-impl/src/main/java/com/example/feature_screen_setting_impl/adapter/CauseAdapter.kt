package com.example.feature_screen_setting_impl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.feature_screen_setting_impl.databinding.ItemCauseBinding
import com.neurotech.core_database_api.model.Cause

internal class CauseAdapter :
    ListAdapter<Cause, CauseAdapter.CauseAdapterHolder>(CauseItemCallBack()) {

    interface CauseAdapterCallback{
        fun deleteCause(cause: Cause)
    }

    var callback: CauseAdapterCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CauseAdapterHolder {
        return CauseAdapterHolder(
            ItemCauseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CauseAdapterHolder, position: Int) {
        holder.bind(getItem(position))
    }

    internal inner class CauseAdapterHolder(
        private val binding: ItemCauseBinding
    ) : ViewHolder(binding.root) {
        fun bind(cause: Cause){
            binding.titleStress.text = cause.name
            when(cause.name){
                "Артефакты"-> binding.deletedSource.isVisible = false
                "Сон"-> binding.deletedSource.isVisible = false
                else -> binding.deletedSource.isVisible = true
            }
            binding.deletedSource.setOnClickListener {
                callback?.deleteCause(cause)
            }
        }
    }
}

class CauseItemCallBack : DiffUtil.ItemCallback<Cause>() {
    override fun areItemsTheSame(oldItem: Cause, newItem: Cause): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Cause, newItem: Cause): Boolean = oldItem == newItem

}