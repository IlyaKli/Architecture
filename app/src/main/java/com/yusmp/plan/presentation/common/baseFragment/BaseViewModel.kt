package com.yusmp.plan.presentation.common.baseFragment

import androidx.lifecycle.ViewModel
import com.yusmp.plan.presentation.common.models.AppEvent
import com.yusmp.plan.presentation.common.models.UiEvent
import com.yusmp.plan.presentation.common.models.UiState
import com.yusmp.plan.presentation.noInternet.NoInternetFragment
import com.yusmp.domain.common.model.CommonBackendFailure
import com.yusmp.domain.common.model.NoInternetFailure
import com.yusmp.domain.common.model.UnknownFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S : UiState, E : UiEvent>(initialState: S) : ViewModel() {

    private val _appWideEvents = MutableStateFlow(emptyList<AppEvent>())
    internal val appWideEvents = _appWideEvents.asStateFlow()

    private val _uiState = MutableStateFlow(initialState)
    internal val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableStateFlow(emptyList<E>())
    internal val uiEvents = _uiEvents.asStateFlow()

    val currentState: S
        get() = _uiState.value

    fun updateUiState(block: S.() -> S) {
        _uiState.update { block(it) }
    }

    fun removeEvent(eventId: String) {
        _uiEvents.update { uiEvents -> uiEvents.filterNot { it.id == eventId } }
    }

    fun sendUiEvent(event: E) {
        _uiEvents.update { it + event }
    }

    fun removeAppWideEvent(eventId: String) {
        _appWideEvents.update { uiEvents -> uiEvents.filterNot { it.id == eventId } }
    }

    fun handleError(throwable: Throwable) {
        val error = when (throwable) {
            is CommonBackendFailure -> AppEvent.ErrorMessage(throwable.apiError?.message ?: "")
            is UnknownFailure -> AppEvent.Unknown
            is NoInternetFailure -> AppEvent.NoInternet
            else -> AppEvent.Unknown
        }
        _appWideEvents.update { it + error }
        // TODO: uncomment when add firebase json.
        //   be aware that if firebase crashlytics is not properly configured, this line will throw
        //   IllegalStateException: Default FirebaseApp is not initialized in this process com.yusmp.plan. Make sure to call FirebaseApp.initializeApp(Context) first.
//        throwable.logToFirebase("handleError")
    }

    /**
     * Refreshes data in the viewModel after user clicks on update in [NoInternetFragment].
     *
     * It is recommended to add your initial data loading logic in refresh function, and call it like this:
     * init {
     *   refresh(false/true)
     * }
     * This ensures that when this function is called after internet loss, it will request all data again.
     *
     * @param isUpdateAll Optional parameter that controls whether to refresh all data
     * or exclude some data from being refreshed in case being called from viewModel init block.
     */
    abstract fun refresh(isUpdateAll: Boolean = true)

}