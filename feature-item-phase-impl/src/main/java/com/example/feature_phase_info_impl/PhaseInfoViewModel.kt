package com.example.feature_phase_info_impl

import androidx.lifecycle.*
import com.neurotech.core_database_api.PhaseApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.UserApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Provider

class PhaseInfoViewModel(
    private val phaseApi: PhaseApi,
    private val resultApi: ResultApi,
    private val userApi: UserApi
) : ViewModel() {

    val resultsInHour = liveData {
        resultApi.getResultTenMinuteInLastHour().collect {
            emit(it)
        }
    }

    val user get() = runBlocking { userApi.getUser() }

    private val _phaseCount = MutableSharedFlow<Int>()
    val phaseCount get() = _phaseCount.asLiveData()
    private val jobList: MutableList<Job> = mutableListOf()

    fun setInterval(interval: Interval) {
        when (interval) {
            Interval.TEN_MINUTE -> {
                jobStop()
                jobList.add(viewModelScope.launch {
                    phaseApi.getPhaseCountInTenMinute().collect {
                        _phaseCount.emit(it)
                    }
                })
            }
            Interval.HOUR -> {
                jobStop()
                jobList.add(viewModelScope.launch {
                    phaseApi.getPhaseCountInHour().collect {
                        _phaseCount.emit(it)
                    }
                })
            }
            Interval.DAY -> {
                jobStop()
                jobList.add(viewModelScope.launch {
                    phaseApi.getPhaseCountInDay().collect {
                        _phaseCount.emit(it)
                    }
                })
            }
        }
    }

    private fun jobStop() {
        jobList.forEach { it.cancel() }
        jobList.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val phaseApi: Provider<PhaseApi>,
        private val resultApi: Provider<ResultApi>,
        private val userApi: Provider<UserApi>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == PhaseInfoViewModel::class.java)
            return PhaseInfoViewModel(phaseApi.get(), resultApi.get(), userApi.get()) as T
        }
    }
}
