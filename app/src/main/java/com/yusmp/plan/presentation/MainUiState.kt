package com.yusmp.plan.presentation

import com.yusmp.plan.presentation.common.models.UiEvent
import com.yusmp.plan.presentation.common.models.UiState

object MainUiState : UiState

sealed class MainUiEvent : UiEvent() {
    object SetHomeAsStartDestination : MainUiEvent()
    object SetAuthAsStartDestination : MainUiEvent()
    object ObserveDeviceShake : MainUiEvent()
}