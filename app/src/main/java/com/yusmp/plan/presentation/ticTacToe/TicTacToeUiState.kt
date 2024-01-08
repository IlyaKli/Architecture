package com.yusmp.plan.presentation.ticTacToe

import com.yusmp.plan.presentation.common.models.UiEvent
import com.yusmp.plan.presentation.common.models.UiState

data class TicTacToeUiState(
    val isLoading: Boolean = true,
) : UiState

sealed class TicTacToeUiEvent : UiEvent() {

    object NavigateToAuthorization : TicTacToeUiEvent()
}