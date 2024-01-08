package com.yusmp.plan.presentation.auth.phone

import android.annotation.SuppressLint
import android.telephony.PhoneNumberUtils
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.yusmp.plan.AuthorizationNavGraphArgs
import com.yusmp.plan.R
import com.yusmp.plan.presentation.auth.phone.model.CountryPhoneCode
import com.yusmp.plan.presentation.common.baseFragment.BaseViewModel
import com.yusmp.domain.auth.LoginParams
import com.yusmp.domain.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val phoneNumberUtil: PhoneNumberUtil,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<PhoneAuthUiState, PhoneAuthUiEvent>(
    PhoneAuthUiState()
) {

    private val args = AuthorizationNavGraphArgs.fromSavedStateHandle(savedStateHandle)

    override fun refresh(isUpdateAll: Boolean) = Unit

    fun updatePolicyAgreementCheckboxState(isChecked: Boolean) {
        updateUiState { copy(hasUserAcceptedPolicy = isChecked) }
    }

    fun registerByPhone() {
        loginUseCase(LoginParams(PhoneNumberUtils.normalizeNumber(currentState.userPhoneNumber)))
            .onStart { updateUiState { copy(isLoading = true) } }
            .onCompletion { updateUiState { copy(isLoading = false) } }
            .onEach { result ->
                result.onFailure { handleError(it) }
                result.onSuccess {
                    sendUiEvent(
                        if (args.isFirstLaunch) PhoneAuthUiEvent.OpenMainScreen else PhoneAuthUiEvent.NavigateUp
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun updatePhoneNumberValue(phoneNumber: String) {
        val parseNumber = try {
            phoneNumberUtil.parse(
                /* numberToParse = */ phoneNumber,
                /* defaultRegion = */ ""
            )
        } catch (e: Exception) {
            phoneNumberUtil.getInvalidExampleNumber("")
        }
        val isNumberValid = try {
            phoneNumberUtil.isValidNumber(/* number = */ parseNumber)
        } catch (e: Exception) {
            false
        }
        val errorMessageId = try {
            if (CountryPhoneCode.isCountryCodeSupported(countryCode = parseNumber.countryCode)) null
            else R.string.authorization_phone_incorrect_number
        } catch (e: Exception) {
            null
        }
        updateUiState {
            copy(
                isNumberValid = isNumberValid,
                userPhoneNumber = if (isNumberValid) phoneNumber else "",
                errorMessageId = errorMessageId
            )
        }
    }

    fun handleBackClick() {
        sendUiEvent(
            event = if (args.isFirstLaunch) PhoneAuthUiEvent.OpenMainScreen else PhoneAuthUiEvent.NavigateUp
        )
    }
}