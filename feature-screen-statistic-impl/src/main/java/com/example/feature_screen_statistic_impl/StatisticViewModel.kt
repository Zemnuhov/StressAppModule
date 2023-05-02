package com.example.feature_screen_statistic_impl

import android.util.Log
import androidx.lifecycle.*
import com.cesarferreira.tempo.*
import com.neurotech.core_database_api.RelaxRecordApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.UserApi
import com.neurotech.core_database_api.model.ResultTenMinute
import com.neurotech.core_database_api.model.ResultsTenMinute
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class StatisticViewModel(
    private val result: ResultApi,
    private val userApi: UserApi,
    private val relaxRecodingApi: RelaxRecordApi
): ViewModel() {

    private var _period = Interval.DAY
    val period: Interval get() = _period

    private val _results = MutableLiveData<List<ResultStatistic>>()
    val results: LiveData<List<ResultStatistic>> get() = _results

    private val _dateFlow = MutableLiveData<String>()
    val dateFlow: LiveData<String> get() = _dateFlow

    private val _markers = MutableLiveData<Markers>()
    val markers: LiveData<Markers> get() = _markers

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var date = Tempo.now
    private val xLabelOfDay = mutableListOf<String>().apply {
        var time = Tempo.now.beginningOfDay
        while (time < date.endOfDay){
            add(time.toString("HH:mm"))
            time += 10.minute
        }
    }



    val user = runBlocking {
        userApi.getUser()
    }


    var state = 1

    init {
        setDayResults()
        Log.e("Time", xLabelOfDay.toString())
        _dateFlow.postValue(date.toString("EE " + TimeFormat.datePattern))
    }

    fun deleteMarkupByTime(time: Date){
        viewModelScope.launch {
            result.deleteMarkupByTime(time)
        }
    }

    fun setKeepByTime(time: Date, keep:String?){
        scope.launch {
            result.setKeepByTime(keep, time)
        }
    }

    fun setDayResults(){
        _period = Interval.DAY
        date = Tempo.now
        updateData()
    }

    fun setWeekResults(){
        _period = Interval.WEEK
        date = Tempo.now
        updateData()

    }

    fun setMonthResults(){
        _period = Interval.MONTH
        date = Tempo.now
        updateData()
    }

    fun goToPrevious(){
        when(period){
            Interval.DAY -> {
                date -= 1.day
                updateData()
            }
            Interval.WEEK -> {
                date -= 7.day
                updateData()
            }
            Interval.MONTH -> {
                date = date.beginningOfMonth - 1.minute
                updateData()
            }
        }


    }

    fun goToNext(){
        when(period){
            Interval.DAY -> {
                if(date.beginningOfDay != Tempo.now.beginningOfDay){
                    date += 1.day
                    updateData()
                }
            }
            Interval.WEEK -> {
                if(date.beginningOfDay != Tempo.now.beginningOfDay) {
                    date += 7.day
                    updateData()
                }
            }
            Interval.MONTH -> {
                if(date.beginningOfMonth != Tempo.now.beginningOfMonth) {
                    date = date.endOfMonth + 1.minute
                    updateData()
                }
            }
        }
    }

    private suspend fun markerUpdate(resultsTenMinute: ResultsTenMinute){
        val relaxMarker =
            relaxRecodingApi.getRelaxRecordByDates(listOf(date)).first().value.isNotEmpty()
        var markupMarker = true
        var commentMarker = false
        with(resultsTenMinute.list){
            forEach {
                if(it.peakCount > user.phaseNormal && it.stressCause == null){
                    markupMarker = false
                }
                if(it.keep != null){
                    commentMarker = true
                }
            }
        }
        _markers.postValue(Markers(relaxMarker, markupMarker, commentMarker))


    }



    private fun updateData(){
        job?.cancel()
        when(period){
            Interval.DAY ->{
                job = scope.launch {
                    state = 1
                    result.getResultsInInterval(date.beginningOfDay, date.endOfDay).collect { resultsTenMinute ->
                        markerUpdate(resultsTenMinute)
                        with(resultsTenMinute.list){
                            val resultTimes = this.map { it.time.toString("HH:mm") }
                            val resultMutableList = this.toMutableList()
                            xLabelOfDay.forEach {
                                if(it !in resultTimes){
                                    resultMutableList.add(ResultTenMinute(
                                        "${date.toString(TimeFormat.dateIsoPattern)} $it:00.000".toDate(TimeFormat.dateTimeIsoPattern),
                                        0,0,1,null, null)
                                    )
                                }
                            }
                            _results.postValue(this.sortedBy { it.time }.map {
                                ResultStatistic(
                                    it.time,
                                    it.peakCount,
                                    it.tonicAvg,
                                    it.conditionAssessment,
                                    it.stressCause,
                                    it.keep
                                )
                            })
                        }

                    }
                }
                _dateFlow.postValue(date.toString("EE " +TimeFormat.datePattern))
            }
            Interval.WEEK -> {
                state = 2
                job = scope.launch {
                    val beginWeek: Date = if(date.isMonday){
                        date.beginningOfDay
                    }else{
                        var count = 1
                        while (!(date-count.day).isMonday ){
                            count++
                        }
                        (date-count.day).beginningOfDay
                    }
                    val endWeek = beginWeek + 7.day - 1.minute
                    _dateFlow.postValue("${beginWeek.toString(TimeFormat.datePattern)} - ${endWeek.toString(TimeFormat.datePattern)}")
                    result.getResultHourByInterval(beginWeek,endWeek).collect { resultsHour ->
                        _results.postValue(resultsHour.list.map {
                            ResultStatistic(
                                it.date,
                                it.peaks,
                                it.tonic,
                                1,
                                it.stressCause,
                                null
                            )
                        })
                    }
                }
            }
            Interval.MONTH -> {
                state = 3
                job = scope.launch {
                    result.getMonthlyResults(date.beginningOfMonth,false).collect { resultsDay ->
                        _results.postValue(resultsDay.list.map {
                            ResultStatistic(
                                it.date,
                                it.peaks,
                                it.tonic,
                                1,
                                it.stressCause,
                                null
                            )
                        })
                    }
                }
                _dateFlow.postValue("${date.beginningOfMonth.toString(TimeFormat.datePattern)} - ${date.endOfMonth.toString(TimeFormat.datePattern)}")
            }
        }
    }



    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val resultApi: Provider<ResultApi>,
        private val userApi: Provider<UserApi>,
        private val relaxRecodingApi: Provider<RelaxRecordApi>
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == StatisticViewModel::class.java)
            return StatisticViewModel(
                resultApi.get(),
                userApi.get(),
                relaxRecodingApi.get()
            ) as T
        }
    }
}