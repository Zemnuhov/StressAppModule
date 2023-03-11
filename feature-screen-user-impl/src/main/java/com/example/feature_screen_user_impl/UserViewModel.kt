package com.example.feature_screen_user_impl

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.cesarferreira.tempo.*
import com.example.core_firebase_auth.FirebaseAuthApi
import com.neurotech.core_database_api.ResultApi
import com.neurotech.core_database_api.UserApi
import com.neurotech.core_database_api.model.UserParameters
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class UserViewModel(
    private val userApi: UserApi,
    private val resultApi: ResultApi,
    private val firebaseAuthApi: FirebaseAuthApi
): ViewModel() {

    val firebaseUser = liveData {
        firebaseAuthApi.user.collect{
            emit(it)
        }
    }
    val user = runBlocking { userApi.getUser() }
    private val _userParameter = MutableLiveData<UserParameters>()
    val userParameter: LiveData<UserParameters> get() = _userParameter

    private var userParametersJob: Job? = null

    init {
        userParametersJob = viewModelScope.launch {
            userApi.getUserParametersByInterval(Tempo.now - 1.day, Tempo.now).collect{
                _userParameter.postValue(it)
            }
        }
    }

    fun register(fragment: UserFragment){
        firebaseAuthApi.registerResultActivity(fragment)
    }


    fun singInWithGoogle(){
        viewModelScope.launch {
            firebaseAuthApi.singInWithGoogle()
        }
    }

    fun singOutWithGoogle(){
        viewModelScope.launch {
            firebaseAuthApi.singOutWithGoogle()
        }
    }

    fun setOneDayInterval(){
        userParametersJob?.cancel()
        userParametersJob = viewModelScope.launch {
            userApi.getUserParametersByInterval(Tempo.now - 1.day, Tempo.now).collect{
                _userParameter.postValue(it)
            }
        }
    }

    fun setOneMonthInterval(){
        userParametersJob?.cancel()
        userParametersJob = viewModelScope.launch {
            userApi.getUserParametersByInterval(Tempo.now - 31.day, Tempo.now.endOfDay).collect{
                _userParameter.postValue(it)
            }
        }
    }

    fun setOneYearInterval(){
        userParametersJob?.cancel()
        userParametersJob = viewModelScope.launch {
            userApi.getUserParametersByInterval(Tempo.now - 1.years, Tempo.now.endOfDay).collect{
                _userParameter.postValue(it)
            }
        }
    }

    fun setDateOfBirth(date: Date){
        viewModelScope.launch {
            userApi.setDateOfBirth(date)
        }
    }

    fun setGender(gender: Boolean) {
        viewModelScope.launch {
            userApi.setGender(gender)
        }
    }


    class Factory @Inject constructor(
        private val userApi: Provider<UserApi>,
        private val resultApi: Provider<ResultApi>,
        private val firebaseAuthApi: Provider<FirebaseAuthApi>
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == UserViewModel::class.java)
            return UserViewModel(
                userApi.get(),
                resultApi.get(),
                firebaseAuthApi.get()
            ) as T
        }
    }
}