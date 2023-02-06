package com.example.feature_screen_markup_impl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cesarferreira.tempo.toString
import com.example.feature_screen_markup_impl.databinding.ItemMarkupResultBinding
import com.neurotech.utils.StressLogger.log
import com.neurotech.utils.TimeFormat

internal class ResultAdapter : ListAdapter<ResultAdapterModel,
        ResultAdapter.ResultAdapterViewHolder>(ResultItemCallBack()) {

    var callback: ResultAdapterCallback? = null

    interface ResultAdapterCallback {
        fun isCheckedClick(position: Int, isCheck: Boolean)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultAdapterViewHolder {
        return ResultAdapterViewHolder(
            ItemMarkupResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ResultAdapterViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    internal inner class ResultAdapterViewHolder(
        private val binding: ItemMarkupResultBinding
    ) : ViewHolder(binding.root) {
        fun bind(resultTenMinute: ResultAdapterModel, position: Int) {
            binding.timeTextView.text = resultTenMinute.time.toString(TimeFormat.timePattern)
            binding.dateTextView.text = resultTenMinute.time.toString(TimeFormat.datePattern)
            binding.phaseTextView.text = resultTenMinute.peakCount.toString()
            binding.tonicTextView.text = resultTenMinute.tonicAvg.toString()
            binding.sourceTextView.text = resultTenMinute.stressCause?: ""
            binding.checkBox.isChecked = resultTenMinute.isChecked
            binding.colorIndicator.background =
                binding.colorIndicator.background.current.apply {
                        setTint(resultTenMinute.color)
                }

            binding.checkBox.setOnClickListener {
                callback?.isCheckedClick(position, binding.checkBox.isChecked)
            }


        }
    }
}

class ResultItemCallBack : DiffUtil.ItemCallback<ResultAdapterModel>() {

    override fun areItemsTheSame(
        oldItem: ResultAdapterModel,
        newItem: ResultAdapterModel
    ): Boolean {
        log("areItemsTheSame    ${oldItem == newItem}")
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ResultAdapterModel,
        newItem: ResultAdapterModel
    ): Boolean {
        log("areContentsTheSame    ${oldItem == newItem}")
        return oldItem == newItem
    }

}