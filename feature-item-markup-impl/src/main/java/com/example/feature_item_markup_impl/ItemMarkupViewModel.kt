package com.example.feature_item_markup_impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.SettingApi
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Provider

class ItemMarkupViewModel(
    private val resultApi: ResultApi,
    private val settingApi: SettingApi
): ViewModel() {

    val countForEachReason = liveData {
        resultApi.getCountForEachCause(settingApi.getCauses().first()).collect{
            emit(it)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val resultTenMinuteApi: Provider<ResultApi>,
        private val settingApi: Provider<SettingApi>,
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == ItemMarkupViewModel::class.java)
            return ItemMarkupViewModel(
                resultTenMinuteApi.get(),
                settingApi.get()
            ) as T
        }
    }
}