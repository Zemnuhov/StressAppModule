package com.neurotech.data.modules.bluetooth.data.filters

class KalmanFilter(
    private var state: Double,
    private var covariance: Double,
    private val F: Double = 1.0,
    private val H: Double = 1.0,
    private val Q: Double = 2.0,
    private val R: Double = 20.0
        //Q в диапазоне от 0-1
        //R величина шумов
) {

    public fun correct(value: Double): Double {
        val x = F * state
        val p = F * covariance * F + Q
        val K = H * p / (H * p * H + R)
        state = x + K * (value - H * x)
        covariance = (1 - K * H) * p
        return state
    }

    public fun correct(value: Int): Double {
        val x = F * state
        val p = F * covariance * F + Q
        val K = H * p / (H * p * H + R)
        state = x + K * (value.toDouble() - H * x)
        covariance = (1 - K * H) * p
        return state
    }

}