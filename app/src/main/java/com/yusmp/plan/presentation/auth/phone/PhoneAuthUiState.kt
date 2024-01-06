package com.yusmp.plan.presentation.auth.phone

import androidx.annotation.StringRes
import com.yusmp.plan.presentation.common.models.UiEvent
import com.yusmp.plan.presentation.common.models.UiState

data class PhoneAuthUiState(
    val hasUserAcceptedPolicy: Boolean = false,
    val userPhoneNumber: String = "",
    @StringRes
    val errorMessageId: Int? = null,
    val isNumberValid: Boolean = false,
    val isLoading: Boolean = false,
) : UiState {

    val maxNumberLength: Int
        get() = if (userPhoneNumber.isBlank()) 16 else userPhoneNumber.length
    val isLoginButtonEnabled: Boolean
        get() = hasUserAcceptedPolicy && userPhoneNumber.isNotEmpty() && isNumberValid && errorMessageId == null
}

sealed class PhoneAuthUiEvent : UiEvent() {

    object OpenMainScreen : PhoneAuthUiEvent()
    object NavigateUp : PhoneAuthUiEvent()
}