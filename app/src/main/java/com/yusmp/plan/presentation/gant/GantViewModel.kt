package com.yusmp.plan.presentation.gant

import com.yusmp.plan.presentation.common.baseFragment.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GantViewModel @Inject constructor(
) : BaseViewModel<GantUiState, GantUiEvent>(GantUiState()) {

    override fun refresh(isUpdateAll: Boolean) = Unit
}