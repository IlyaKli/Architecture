package com.yusmp.plan.presentation.gant

import com.yusmp.plan.presentation.common.models.UiEvent
import com.yusmp.plan.presentation.common.models.UiState

data class GantUiState(
    val isLoading: Boolean = true,
) : UiState

sealed class GantUiEvent : UiEvent() {

    object NavigateToAuthorization : GantUiEvent()
}