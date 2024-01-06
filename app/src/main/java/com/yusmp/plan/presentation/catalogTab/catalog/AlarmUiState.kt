package com.yusmp.plan.presentation.catalogTab.catalog

import com.yusmp.plan.presentation.common.models.UiEvent
import com.yusmp.plan.presentation.common.models.UiState

data class AlarmUiState(
    val isButtonReadyEnabled: Boolean = false,
) : UiState

sealed class AlarmUiEvent : UiEvent() {
    object OpenMainScreen : AlarmUiEvent()
}