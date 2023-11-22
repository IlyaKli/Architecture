package com.yusmp.plan.presentation.profileTab.profile

import com.yusmp.plan.presentation.common.models.UiEvent
import com.yusmp.plan.presentation.common.models.UiState

data class ProfileUiState(
    val phoneNumber: String? = null,
    val isLoading: Boolean = true,
    val isUserAuthorized: Boolean = false,
) : UiState

sealed class ProfileEvent : UiEvent() {
    object NavigateToAuthorization : ProfileEvent()
}