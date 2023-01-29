package com.neurotech.data.modules.bluetooth.data.filters

class ValueConverter {

    private var previousValue = 0

    fun rangeConvert(value: Int, maxRange: Int = 10000): Int{
        return (value * maxRange) / 1023
    }

    fun toPhaseValue(value: Int): Int{
        val result = value - previousValue
        previousValue = value
        return result*5
    }





}