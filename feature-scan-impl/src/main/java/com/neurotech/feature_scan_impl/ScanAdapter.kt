package com.neurotech.feature_scan_impl

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.neurotech.core_ble_device_scan_api.Device
import com.neurotech.feature_scan_impl.databinding.ItemDeviceCardBinding
import com.neurotech.utils.StressLogger.log

internal class ScanAdapter: ListAdapter<Device, ScanAdapter.ScanViewHolder>(DeviceItemCallBack()) {

    interface ClickItemDevice{
        fun clickItem(device: Device)
    }

    var callBack: ClickItemDevice? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        return ScanViewHolder(ItemDeviceCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    internal inner class ScanViewHolder(private val binding: ItemDeviceCardBinding): ViewHolder(binding.root){
        fun bind(device: Device){
            binding.nameDevice.text = device.name
            binding.macDevice.text = device.mac
            binding.root.setOnClickListener {
                log("Selected device Name: ${device.name} Mac: ${device.mac}")
                val increaseAnimationX = ObjectAnimator.ofFloat(it, "scaleX", 0.8F)
                val increaseAnimationY = ObjectAnimator.ofFloat(it, "scaleY", 0.8F)
                val decreaseAnimationX = ObjectAnimator.ofFloat(it, "scaleX", 1F)
                val decreaseAnimationY = ObjectAnimator.ofFloat(it, "scaleY", 1F)
                AnimatorSet().let { animatorSet ->
                    animatorSet.play(increaseAnimationX).with(increaseAnimationY)
                    animatorSet.play(decreaseAnimationX).with(decreaseAnimationY).after(increaseAnimationX)
                    animatorSet.duration = 300
                    animatorSet.interpolator = DecelerateInterpolator()
                    animatorSet.start()
                }
                callBack?.clickItem(device)
            }
        }
    }
}

class DeviceItemCallBack: DiffUtil.ItemCallback<Device>(){
    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        log("$oldItem ---- $newItem")
        return true
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        log("$oldItem ---- $newItem")
        return true
    }

}