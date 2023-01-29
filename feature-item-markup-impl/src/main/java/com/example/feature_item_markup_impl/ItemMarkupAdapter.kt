package com.example.feature_item_markup_impl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.feature_item_markup_impl.databinding.SourceItemBinding
import androidx.recyclerview.widget.ListAdapter

internal class ItemMarkupAdapter: ListAdapter<CountForCauseModelAdapter, ItemMarkupAdapter.ItemMarkupViewHolder>(ItemMarkupCallBack())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMarkupViewHolder {
        return ItemMarkupViewHolder(
            SourceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemMarkupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    internal inner class ItemMarkupViewHolder(private val binding: SourceItemBinding): ViewHolder(binding.root){
        fun bind(countForCauseModelAdapter: CountForCauseModelAdapter){
            binding.imageColorSources.setColorFilter(countForCauseModelAdapter.color)
            val gradientDrawable = binding.imageColorSources.background.current
            binding.imageColorSources.background = gradientDrawable.apply {
                this.setTint(countForCauseModelAdapter.color)
            }
            binding.nameSources.text = countForCauseModelAdapter.cause.name
            binding.countSourcesStatistic.text = countForCauseModelAdapter.count.toString()
        }
    }
}

class ItemMarkupCallBack: DiffUtil.ItemCallback<CountForCauseModelAdapter>(){
    override fun areItemsTheSame(
        oldItem: CountForCauseModelAdapter,
        newItem: CountForCauseModelAdapter
    ): Boolean {
        return true
    }

    override fun areContentsTheSame(
        oldItem: CountForCauseModelAdapter,
        newItem: CountForCauseModelAdapter
    ): Boolean {
        return false
    }


}