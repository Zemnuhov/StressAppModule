package com.example.feature_screen_editing_day_plan_impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.model.DayPlan
import com.neurotech.utils.WorkResult
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class EditingDayPlanViewModel(
    private val settingApi: SettingApi
) : ViewModel() {

    private val _errorHandler = MutableLiveData<WorkResult>()
    val errorHandler: LiveData<WorkResult> = _errorHandler
    val causes = liveData {
        settingApi.getCauses().collect {
            emit(it)
        }
    }


    suspend fun getDayPlanById(id: Int): DayPlan {
        return settingApi.getDayPlanById(id)
    }

    fun updateDayPlan(dayPlan: DayPlan){
        viewModelScope.launch {
            _errorHandler.postValue(settingApi.updateDayPlan(dayPlan))
        }
    }


    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val settingApi: Provider<SettingApi>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == EditingDayPlanViewModel::class.java)
            return EditingDayPlanViewModel(settingApi.get()) as T
        }
    }
}