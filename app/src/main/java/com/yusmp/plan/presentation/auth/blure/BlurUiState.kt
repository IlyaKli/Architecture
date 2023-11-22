package com.yusmp.plan.presentation.auth.blure

import com.yusmp.plan.presentation.common.models.UiEvent
import com.yusmp.plan.presentation.common.models.UiState

data class BlurUiState(
    val workState: WorkState = WorkState.NONE,
) : UiState {
    val isLoading: Boolean
        get() = workState == WorkState.PROGRESS
}

sealed class BlurUiEvent : UiEvent() {
    object OpenMainScreen : BlurUiEvent()
}

enum class WorkState {
    NONE,
    PROGRESS,
    FINISHED
}