package com.yusmp.plan.presentation.noInternet

import com.yusmp.plan.presentation.common.baseFragment.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoInternetViewModel @Inject constructor() :
    BaseViewModel<NoInternetUiState, NoInternetUiEvent>(NoInternetUiState) {

    override fun refresh(isUpdateAll: Boolean) = Unit
}