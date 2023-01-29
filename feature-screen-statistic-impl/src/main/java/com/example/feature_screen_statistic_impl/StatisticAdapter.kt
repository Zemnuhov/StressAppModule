package com.example.feature_screen_statistic_impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cesarferreira.tempo.toString
import com.example.feature_screen_statistic_impl.databinding.ItemStatisticResultBinding
import com.neurotech.utils.TimeFormat
import java.util.*

class StatisticAdapter: ListAdapter<ResultStatistic, StatisticAdapter.StatisticViewHolder>(StatisticAdapterCallBack()) {

    val keepMap = mutableMapOf<Date, String?>()
    var normalValue: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticViewHolder {
        return StatisticViewHolder(
            ItemStatisticResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StatisticViewHolder, position: Int) {
        val result = getItem(position)
        holder.textBind(result)
        holder.colorBind(result)
        holder.keepSaveButton.setOnClickListener {
            if(holder.keepEditText.text.isNotEmpty()){
                keepMap[result.time] = holder.keepEditText.text.toString()
            }else{
                keepMap[result.time] = null
            }
            holder.saveButtonClick()
        }
    }



    inner class StatisticViewHolder(private val itemBinding: ItemStatisticResultBinding): RecyclerView.ViewHolder(itemBinding.root){
        val keepEditText = itemBinding.keepEditText
        val keepSaveButton = itemBinding.keepSaveButton
        fun textBind(result: ResultStatistic){
            itemBinding.timeStatisticTextView.text = result.time.toString(TimeFormat.timePattern)
            itemBinding.dateStatisticTextView.text = result.time.toString(TimeFormat.datePattern)
            itemBinding.peakCountStatisticTextView.text = result.peakCount.toString()
            itemBinding.avgTonicStatisticTextView.text = result.tonicAvg.toString()
            itemBinding.sourceStatisticTextView.text = result.stressCause ?: ""
            if(result.keep != null){
                showTextLayout(result.keep)
            }
            itemBinding.keepButton.setOnClickListener {
                if(itemBinding.keepEditText.visibility == View.GONE){
                    showEditLayout()
                }else{
                    if (result.keep != null){
                        showTextLayout(result.keep)
                    }else{
                        goneAll()
                    }
                }
            }
        }

        private fun goneAll(){
            itemBinding.keepLayout.visibility = View.GONE
            itemBinding.keepEditText.visibility = View.GONE
            itemBinding.keepSaveButton.visibility = View.GONE
            itemBinding.keepTextView.visibility = View.GONE
            itemBinding.keepString.visibility = View.GONE
        }

        private fun showEditLayout(){
            itemBinding.keepLayout.visibility = View.VISIBLE
            itemBinding.keepTextView.visibility = View.GONE
            itemBinding.keepString.visibility = View.GONE
            itemBinding.keepEditText.visibility = View.VISIBLE
            itemBinding.keepSaveButton.visibility = View.VISIBLE
            itemBinding.keepEditText.hint = "Запишите заметку"
        }

        private fun showTextLayout(keep: String){
            itemBinding.keepTextView.text = keep
            itemBinding.keepLayout.visibility = View.VISIBLE
            itemBinding.keepEditText.visibility = View.GONE
            itemBinding.keepSaveButton.visibility = View.GONE
            itemBinding.keepTextView.visibility = View.VISIBLE
            itemBinding.keepString.visibility = View.VISIBLE
        }

        fun saveButtonClick(){
            if (itemBinding.keepEditText.text.isNotEmpty()){
                showTextLayout(itemBinding.keepEditText.text.toString())
            }else{
                goneAll()
            }

        }

        fun colorBind(result: ResultStatistic){
            val gradientDrawable = itemBinding.colorLayoutStatisticTextView.background.current
            when (result.peakCount) {
                in 0..normalValue -> {
                    itemBinding.colorLayoutStatisticTextView.background = gradientDrawable.apply {
                        this.setTint(ContextCompat.getColor(itemBinding.root.context, R.color.green_active))
                    }
                }
                in normalValue..normalValue*2 -> {
                    itemBinding.colorLayoutStatisticTextView.background = gradientDrawable.apply {
                        this.setTint(ContextCompat.getColor(itemBinding.root.context, R.color.yellow_active))
                    }
                }
                else -> {
                    itemBinding.colorLayoutStatisticTextView.background = gradientDrawable.apply {
                        this.setTint(ContextCompat.getColor(itemBinding.root.context, R.color.red_active))
                    }
                }
            }
        }
    }
}

class StatisticAdapterCallBack: DiffUtil.ItemCallback<ResultStatistic>(){
    override fun areItemsTheSame(
        oldItem: ResultStatistic,
        newItem: ResultStatistic
    ): Boolean {
        return true
    }

    override fun areContentsTheSame(
        oldItem: ResultStatistic,
        newItem: ResultStatistic
    ): Boolean {
        return false
    }


}