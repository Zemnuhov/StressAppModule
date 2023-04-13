package com.example.feature_screen_analitic_impl

import android.util.Log
import androidx.lifecycle.*
import com.cesarferreira.tempo.*
import com.neurotech.core_database_api.RelaxRecordApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.UserApi
import com.neurotech.core_database_api.model.*
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
    private val settingApi: SettingApi,
    private val relaxRecodingApi: RelaxRecordApi
) : ViewModel() {

    private val _resultsInMonth = MutableLiveData<ResultsDay>()
    val resultsInMonth: LiveData<ResultsDay> get() = _resultsInMonth

    private val _resultsInInterval = MutableLiveData<CountForEachCause>()
    val resultsInInterval: LiveData<CountForEachCause> get() = _resultsInInterval

    private val _dayRatingList = MutableLiveData<List<Double>>()
    val dayRatingList: LiveData<List<Double>> get() = _dayRatingList

    private val _correctedDayRatingList = MutableLiveData<List<Double>>()
    val correctedDayRatingList: LiveData<List<Double>> get() = _correctedDayRatingList

    private val _userRating = MutableLiveData<Int>()
    val userRating: LiveData<Int> get() = _userRating

    val user = runBlocking { userApi.getUser() }


    private var _monthDate = Tempo.now
    val monthDate get() = _monthDate

    private var monthJob: Job = viewModelScope.launch {
        resultApi.getMonthlyResults(_monthDate, true).collect {
            _resultsInMonth.postValue(it)
        }
    }

    init {
        viewModelScope.launch {
            resultApi.getLastFiveValidDay().collect {
                val dayRating = it.list.map {
                    calculateResultToEvaluation(it)
                }
                val relaxRecords =
                    relaxRecodingApi.getRelaxRecordByDates(it.list.map { it.date }).first()
                val correctedDayRating = it.list.map { result ->
                    val value = calculateResultToEvaluation(result) - sumByDate(relaxRecords, result.date)
                    return@map if(value>1){ value }else{ 1.0 }
                }
                updateEvaluationGraph(dayRating, correctedDayRating)
            }


        }
    }

    private fun sumByDate(relaxRecords: RelaxRecords, date: Date): Double{
        var summ = 0.0
        relaxRecords.value.forEach {
            if(date.beginningOfDay == it.date.beginningOfDay){
                summ += (it.tonicAdjusted * 0.0005 - it.phaseCount * 0.005) * (it.relaxationDuration * 0.1)
            }
        }
        return summ
    }


    private suspend fun calculateResultToEvaluation(result: ResultDay): Double {
        val user = userApi.getUser()
        val maxParams = resultApi.getMaxUserParameter()
        val tonicMapped = valueMapper(result.tonic, user.tonicAvg, maxParams.tonic, 1, 5)
        val dayPeaksMapped =
            valueMapper(result.peaks, user.phaseInDayNormal, maxParams.dayPhase, 1, 5)
        val avgPeaksMapped =
            valueMapper(result.peaksAvg, user.phaseNormal, maxParams.tenMinutePhase, 1, 5)
        return (tonicMapped + dayPeaksMapped + avgPeaksMapped) / 3
    }

    private fun updateEvaluationGraph(dayRating: List<Double>, correctedDayRating: List<Double>) {
        _dayRatingList.postValue(dayRating.reversed())
        _correctedDayRatingList.postValue(correctedDayRating.reversed())
        if (dayRating.isNotEmpty()) {
            val rating = dayRating.sum() / dayRating.size
            _userRating.postValue(if (rating < 1) 1 else rating.roundToInt())
        } else {
            _userRating.postValue(0)
        }
    }

    private fun observeData() {
        monthJob.cancel()
        monthJob = viewModelScope.launch {
            resultApi.getMonthlyResults(_monthDate, true).collect {
                _resultsInMonth.postValue(it)
            }
        }
    }

    fun previousMonth() {
        _monthDate = _monthDate.beginningOfMonth - 1.minute
        observeData()
    }

    fun nextMonth() {
        if (_monthDate.beginningOfMonth != Tempo.now.beginningOfMonth) {
            _monthDate = _monthDate.endOfMonth + 1.minute
            observeData()
        }
    }

    fun setInterval(beginInterval: Date, endInterval: Date) {
        viewModelScope.launch {
            resultApi.getCountCauseInInterval(
                settingApi.getCauses().first(),
                beginInterval,
                endInterval
            ).collect {
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
        private val settingApi: Provider<SettingApi>,
        private val relaxRecodingApi: Provider<RelaxRecordApi>,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == AnalyticViewModel::class.java)
            return AnalyticViewModel(
                resultApi.get(),
                userApi.get(),
                settingApi.get(),
                relaxRecodingApi.get()
            ) as T
        }
    }
}