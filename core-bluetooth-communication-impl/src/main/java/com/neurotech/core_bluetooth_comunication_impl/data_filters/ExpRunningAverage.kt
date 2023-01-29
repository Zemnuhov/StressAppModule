package com.neurotech.data.modules.bluetooth.data.filters

class ExpRunningAverage(private val k: Double) {
    var filVal = 0.0

    public fun filter(newVal: Int): Double {
        filVal += (newVal.toDouble() - filVal) * k
        return filVal
    }
    public fun filter(newVal: Double): Double {
        filVal += (newVal.toDouble() - filVal) * k
        return filVal
    }
}