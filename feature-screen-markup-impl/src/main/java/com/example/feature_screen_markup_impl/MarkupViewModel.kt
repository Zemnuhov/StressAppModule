package com.example.feature_screen_markup_impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.SettingApi
import com.neurotech.core_database_api.UserApi
import com.neurotech.core_database_api.model.ResultsTenMinute
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
                userApi.getUserParameters().first().tenMinutePhase
            ).collect {
                emit(it)
            }
    }

    val causes = liveData {
        settingApi.getCauses().collect{
            emit(it)
        }
    }

    val user = runBlocking { userApi.getUser() }

    fun saveMarkups(results: ResultsTenMinute){
        viewModelScope.launch {
            resultApi.updateResultTenMinute(results)
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