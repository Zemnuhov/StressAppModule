package com.example.feature_screen_markup_impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.cesarferreira.tempo.Tempo
import com.cesarferreira.tempo.beginningOfDay
import com.cesarferreira.tempo.endOfDay
import com.cesarferreira.tempo.minute
import com.cesarferreira.tempo.plus
import com.cesarferreira.tempo.toDate
import com.cesarferreira.tempo.toString
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.UserApi
import com.neurotech.core_database_api.model.ResultTenMinute
import com.neurotech.core_database_api.model.ResultsTenMinute
import com.neurotech.utils.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Provider

class MarkupViewModel(
    private val resultApi: ResultApi,
    private val userApi: UserApi,
    private val settingApi: SettingApi
) : ViewModel() {

    val resultForMarkup = liveData {
        resultApi
            .getResultsTenMinuteAboveThreshold(
                userApi.getUser().phaseNormal
            ).collect {
                emit(it)
            }
    }

    val causes = liveData {
        settingApi.getCauses().collect {
            emit(it)
        }
    }

    val user = runBlocking { userApi.getUser() }

    fun saveMarkups(results: ResultsTenMinute) {
        CoroutineScope(Dispatchers.IO).launch {
            resultApi.updateResultTenMinute(results)
        }
    }

    private var date = Tempo.now
    private val xLabelOfDay = mutableListOf<String>().apply {
        var time = Tempo.now.beginningOfDay
        while (time < date.endOfDay) {
            add(time.toString("HH:mm"))
            time += 10.minute
        }
    }

    private val _results = MutableLiveData<ResultsMarkup>()
    val results: LiveData<ResultsMarkup> get() = _results


    init {
        viewModelScope.launch {
            resultApi.getResultsInInterval(date.beginningOfDay, date.endOfDay)
                .collect { resultsTenMinute ->
                    with(resultsTenMinute.list) {
                        val resultTimes = this.map { it.time.toString("HH:mm") }
                        val resultMutableList = this.toMutableList()
                        xLabelOfDay.forEach {
                            if (it !in resultTimes) {
                                resultMutableList.add(
                                    ResultTenMinute(
                                        "${date.toString(TimeFormat.dateIsoPattern)} $it:00.000".toDate(
                                            TimeFormat.dateTimeIsoPattern
                                        ),
                                        0, 0, 1, null, null
                                    )
                                )
                            }
                        }
                        _results.postValue(ResultsMarkup(resultMutableList.sortedBy { it.time }
                            .map {
                                ResultMarkup(
                                    it.time,
                                    it.peakCount,
                                    it.tonicAvg,
                                    it.conditionAssessment,
                                    it.stressCause,
                                    it.keep
                                )
                            }
                        )
                        )
                    }

                }
        }

    }


    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val resultApi: Provider<ResultApi>,
        private val userApi: Provider<UserApi>,
        private val settingApi: Provider<SettingApi>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == MarkupViewModel::class.java)
            return MarkupViewModel(
                resultApi.get(),
                userApi.get(),
                settingApi.get()
            ) as T
        }
    }
}