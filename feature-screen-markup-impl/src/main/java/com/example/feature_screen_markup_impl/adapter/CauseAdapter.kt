package com.example.feature_screen_markup_impl.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.feature_screen_markup_impl.databinding.ItemMarkupCauseBinding
import com.example.feature_screen_markup_impl.databinding.ItemMarkupResultBinding
import com.neurotech.core_database_api.model.Cause
import com.neurotech.utils.StressLogger.log

class CauseAdapter: ListAdapter<Cause,
        CauseAdapter.CauseViewHolder>(CauseItemCallBack()) {

    var callback: CauseCallback? = null

    interface CauseCallback{
        fun clickCause(cause: Cause)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CauseViewHolder {
        return CauseViewHolder(
            ItemMarkupCauseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CauseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class CauseViewHolder(private val binding: ItemMarkupCauseBinding): ViewHolder(binding.root){
        fun bind(cause: Cause){
            binding.sourceInMarkup.text = cause.name
            binding.root.elevation = 10F
            binding.root.setOnClickListener {
                val beginAnimation = ObjectAnimator.ofFloat(it, "elevation", 10F, 0F)
                val endAnimation = ObjectAnimator.ofFloat(it, "elevation", 0F, 10F)
                AnimatorSet().apply {
                    play(beginAnimation).before(endAnimation)
                    play(endAnimation)
                    start()
                }
                callback?.clickCause(cause)
            }
        }
    }
}

class CauseItemCallBack : DiffUtil.ItemCallback<Cause>() {

    override fun areItemsTheSame(
        oldItem: Cause,
        newItem: Cause
    ): Boolean {
        log("areItemsTheSame    ${oldItem == newItem}")
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Cause,
        newItem: Cause
    ): Boolean {
        log("areContentsTheSame    ${oldItem == newItem}")
        return oldItem == newItem
    }

}