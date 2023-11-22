package com.yusmp.plan.presentation.profileTab.profile

import androidx.lifecycle.viewModelScope
import com.yusmp.plan.presentation.common.baseFragment.BaseViewModel
import com.yusmp.domain.auth.LogoutUseCase
import com.yusmp.domain.auth.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileTabViewModel @Inject constructor(
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel<ProfileUiState, ProfileEvent>(ProfileUiState()) {

    init {
        observeAuthorizationState()
    }

    override fun refresh(isUpdateAll: Boolean) = Unit

    private fun observeAuthorizationState() {
        observeSessionUseCase(Unit)
            .onStart { updateUiState { copy(isLoading = true) } }
            .onEach { session ->
                updateUiState {
                    copy(
                        isLoading = false,
                        phoneNumber = session.phone,
                        isUserAuthorized = session.isAuthorized()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun changeAuthorizationState() {
        if (currentState.isUserAuthorized) {
            viewModelScope.launch {
                logoutUseCase(Unit)
            }
        } else {
            sendUiEvent(ProfileEvent.NavigateToAuthorization)
        }
    }
}