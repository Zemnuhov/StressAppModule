package com.example.feature_screen_setting_impl.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_screen_setting_impl.databinding.ItemDayPlanBinding
import com.neurotech.core_database_api.model.DayPlan

internal class DayPlanAdapter:
    ListAdapter<DayPlan, DayPlanAdapter.DayPlanAdapterHolder>(DayPlanItemCallBack()) {

    interface DayPlanAdapterCallback{
        fun deleteDayPlan(dayPlan: DayPlan)
        fun clickToDayPlan(dayPlan: DayPlan)
    }

    var callback: DayPlanAdapterCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayPlanAdapterHolder {
        return DayPlanAdapterHolder(
            ItemDayPlanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DayPlanAdapterHolder, position: Int) {
        holder.bind(getItem(position))
    }

    internal inner class DayPlanAdapterHolder(
        private val binding: ItemDayPlanBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(dayPlan: DayPlan){
            binding.markupTitle.text = dayPlan.plan
            dayPlan.firstSource?.let {
                binding.firstSource.visibility = View.VISIBLE
                binding.firstSource.text = it
            }
            dayPlan.secondSource?.let {
                binding.secondSource.visibility = View.VISIBLE
                binding.secondSource.text = it
            }
            if(dayPlan.timeBegin != null && dayPlan.timeEnd != null){
                binding.timeTextView.text = "${dayPlan.timeBegin}-${dayPlan.timeEnd}"
            }else{
                binding.timeTextView.text = "Время не задано"
            }
            binding.root.setOnClickListener {
                callback?.clickToDayPlan(dayPlan)
            }
            binding.deleteMarkupButton.setOnClickListener {
                callback?.deleteDayPlan(dayPlan)
            }
        }
    }
}

class DayPlanItemCallBack : DiffUtil.ItemCallback<DayPlan>() {
    override fun areItemsTheSame(oldItem: DayPlan, newItem: DayPlan): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: DayPlan, newItem: DayPlan): Boolean = oldItem == newItem

}