package com.example.feature_screen_analitic_impl

import android.util.Log
import androidx.lifecycle.*
import com.cesarferreira.tempo.*
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.UserApi
import com.neurotech.core_database_api.model.CountForEachCause
import com.neurotech.core_database_api.model.ResultsDay
import com.neurotech.core_database_api.model.ResultsTenMinute
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt

class AnalyticViewModel(
    private val resultApi: ResultApi,
    private val userApi: UserApi,
    private val settingApi: SettingApi
): ViewModel() {

    private val _resultsInMonth = MutableLiveData<ResultsDay>()
    val resultsInMonth: LiveData<ResultsDay> get() = _resultsInMonth

    private val _resultsInInterval = MutableLiveData<CountForEachCause>()
    val resultsInInterval: LiveData<CountForEachCause> get() = _resultsInInterval

    private val _dayRatingList = MutableLiveData<List<Double>>()
    val dayRatingList: LiveData<List<Double>> get() = _dayRatingList

    private val _userRating = MutableLiveData<Int>()
    val userRating: LiveData<Int> get() = _userRating

    val user = runBlocking { userApi.getUser() }


    private var _monthDate = Tempo.now
    val monthDate get() = _monthDate

    private var monthJob: Job = viewModelScope.launch {
        resultApi.getMonthlyResults(_monthDate,true).collect {
            _resultsInMonth.postValue(it)
        }
    }

    init {
        viewModelScope.launch {
            val user = userApi.getUser()
            resultApi.getLastFiveValidDay().collect{
                val maxParams = resultApi.getMaxUserParameter()
                val dayRating = it.list.map {
                    val tonicMapped = valueMapper(it.tonic, user.tonicAvg, maxParams.tonic, 1 , 5)
                    val dayPeaksMapped = valueMapper(it.peaks, user.phaseInDayNormal, maxParams.dayPhase, 1 , 5)
                    val avgPeaksMapped = valueMapper(it.peaksAvg, user.phaseNormal, maxParams.tenMinutePhase, 1 , 5)
                    return@map (tonicMapped + dayPeaksMapped + avgPeaksMapped)/3
                }
                _dayRatingList.postValue(dayRating.reversed())
                if (dayRating.isNotEmpty()){
                    val rating = dayRating.sum()/dayRating.size
                    _userRating.postValue(if (rating<1) 1 else rating.roundToInt())
                }else{
                    _userRating.postValue(0)
                }

            }
        }
    }

    private fun observeData(){
        monthJob.cancel()
        monthJob = viewModelScope.launch {
            resultApi.getMonthlyResults(_monthDate,true).collect {
                _resultsInMonth.postValue(it)
            }
        }
    }

    fun previousMonth(){
        _monthDate = _monthDate.beginningOfMonth - 1.minute
        observeData()
    }

    fun nextMonth(){
        if(_monthDate.beginningOfMonth != Tempo.now.beginningOfMonth){
            _monthDate = _monthDate.endOfMonth + 1.minute
            observeData()
        }
    }

    fun setInterval(beginInterval: Date, endInterval: Date){
        viewModelScope.launch {
            resultApi.getCountCauseInInterval(settingApi.getCauses().first(),beginInterval, endInterval).collect{
                _resultsInInterval.postValue(it)
            }
        }
    }

    private fun valueMapper(value: Int, smin: Int, smax: Int, dmin: Int, dmax: Int): Double {
        return (value.toDouble() - smin.toDouble()) / (smax.toDouble() - smin.toDouble()) * (dmax.toDouble() - dmin.toDouble()) + dmin.toDouble()
    }


    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val resultApi: Provider<ResultApi>,
        private val userApi: Provider<UserApi>,
        private val settingApi: Provider<SettingApi>
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == AnalyticViewModel::class.java)
            return AnalyticViewModel(
                resultApi.get(),
                userApi.get(),
                settingApi.get()
            ) as T
        }
    }
}