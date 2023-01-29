package com.example.feature_screen_setting_impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.model.Cause
import com.neurotech.core_database_api.model.DayPlan
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class SettingViewModel(
    private val setting: SettingApi
): ViewModel() {

    val causes = liveData {
        setting.getCauses().collect{
            emit(it)
        }
    }

    val dayPlans = liveData {
        setting.getDayPlans().collect{
            emit(it)
        }
    }

    fun addCause(cause: Cause){
        viewModelScope.launch {
            setting.addCause(cause)
        }
    }

    fun deleteCause(cause: Cause){
        viewModelScope.launch {
            setting.deleteCause(cause)
        }
    }

    fun addDayPlan(planName: String){
        viewModelScope.launch {
            setting.addDayPlan(
                DayPlan(
                    0,planName,null,null,null,null, false
                )
            )
        }
    }

    fun deleteDayPlan(dayPlan: DayPlan){
        viewModelScope.launch {
            setting.deleteDayPlan(dayPlan)
        }
    }





    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val setting: Provider<SettingApi>
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == SettingViewModel::class.java)
            return SettingViewModel(setting.get()) as T
        }
    }
}