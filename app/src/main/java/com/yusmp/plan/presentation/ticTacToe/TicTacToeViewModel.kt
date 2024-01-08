package com.yusmp.plan.presentation.ticTacToe

import com.yusmp.plan.presentation.common.baseFragment.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TicTacToeViewModel @Inject constructor(
) : BaseViewModel<TicTacToeUiState, TicTacToeUiEvent>(TicTacToeUiState()) {

    override fun refresh(isUpdateAll: Boolean) = Unit
}