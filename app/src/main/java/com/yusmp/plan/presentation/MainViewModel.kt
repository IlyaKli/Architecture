package com.yusmp.plan.presentation

import androidx.lifecycle.viewModelScope
import com.yusmp.plan.presentation.common.baseFragment.BaseViewModel
import com.yusmp.domain.dataStore.GetIsFirstLaunchAndSetFalseUseCase
import com.yusmp.domain.debugging.LaunchChuckerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getIsFirstLaunchAndSetFalseUseCase: GetIsFirstLaunchAndSetFalseUseCase,
    isDebugEnvironment: Boolean,
    private val launchChuckerUseCase: LaunchChuckerUseCase,
) : BaseViewModel<MainUiState, MainUiEvent>(MainUiState) {

    init {
        viewModelScope.launch {
            when (getIsFirstLaunchAndSetFalseUseCase(Unit)) {
                true -> sendUiEvent(MainUiEvent.SetAuthAsStartDestination)
                false -> sendUiEvent(MainUiEvent.SetHomeAsStartDestination)
            }
        }
        if (isDebugEnvironment) sendUiEvent(MainUiEvent.ObserveDeviceShake)
    }

    override fun refresh(isUpdateAll: Boolean) = Unit

    fun launchChuckerActivity() = launchChuckerUseCase()
}