package com.example.core_screen_controller_impl

import com.example.core_screen_controller.ScreenControllerApi
import com.example.core_screen_controller.ScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ScreenController: ScreenControllerApi {

    private val screenState = MutableStateFlow(ScreenState.CREATE)

    override suspend fun getMainScreenStateFlow(): Flow<ScreenState> {
        return screenState
    }

    override fun setState(state: ScreenState) {
        screenState.value = state
    }
}