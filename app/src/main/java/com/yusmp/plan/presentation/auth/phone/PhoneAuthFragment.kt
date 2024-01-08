package com.yusmp.plan.presentation.auth.phone

import android.content.Context
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.badoo.mvicore.modelWatcher
import com.yusmp.plan.R
import com.yusmp.plan.databinding.FragmentPhoneAuthBinding
import com.yusmp.plan.presentation.auth.phone.model.CountryPhoneCode
import com.yusmp.plan.presentation.common.baseFragment.BaseFragment
import com.yusmp.plan.presentation.common.extentions.ClickablePart
import com.yusmp.plan.presentation.common.extentions.changeErrorState
import com.yusmp.plan.presentation.common.extentions.handleBackClick
import com.yusmp.plan.presentation.common.extentions.openLinkInBrowser
import com.yusmp.plan.presentation.common.extentions.setClickableParts
import com.yusmp.plan.presentation.common.extentions.setHomeAsStartDestination
import com.yusmp.plan.presentation.common.extentions.setMaxLength
import com.yusmp.plan.presentation.common.utils.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoneAuthFragment :
    BaseFragment<FragmentPhoneAuthBinding, PhoneAuthUiState, PhoneAuthUiEvent>() {

    override val viewModel: PhoneAuthViewModel by viewModels()

    override val stateRenderer = modelWatcher {
        PhoneAuthUiState::hasUserAcceptedPolicy { isChecked ->
            binding.chkPolicy.isChecked = isChecked
        }
        PhoneAuthUiState::errorMessageId { errorMessageId ->
            binding.ilPhoneNumber.changeErrorState(errorMessageId?.let(::getString))
        }
        PhoneAuthUiState::isLoginButtonEnabled { isEnabled ->
            binding.btnLogin.isEnabled = isEnabled
        }
        PhoneAuthUiState::isLoading { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
        PhoneAuthUiState::maxNumberLength { maxNumberLength ->
            binding.etPhoneNumber.setMaxLength(maxNumberLength)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        handleBackClick { viewModel.handleBackClick() }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPhoneAuthBinding {
        return FragmentPhoneAuthBinding.inflate(inflater, container, false)
    }

    override fun PhoneAuthUiEvent.handleEvent() {
        when (this) {
            PhoneAuthUiEvent.OpenMainScreen -> setHomeAsStartDestination()
            PhoneAuthUiEvent.NavigateUp -> findNavController().navigateUp()
        }
    }

    override fun FragmentPhoneAuthBinding.setupViews() {
        with(binding) {
            tvPolicy.setClickableParts(
                completeTextId = R.string.authorization_phone_policy,
                clickableParts = listOf(
                    ClickablePart(
                        textId = R.string.authorization_phone_company_rules,
                        clickListener = { openLinkInBrowser("https://www.google.com") }
                    )
                )
            )
            etPhoneNumber.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && etPhoneNumber.text.isNullOrBlank()) etPhoneNumber.setText(
                    CountryPhoneCode.PLUS_PREFIX
                )
            }
            etPhoneNumber.doAfterTextChanged { text ->
                viewModel.updatePhoneNumberValue(text.toString())
            }
            btnLogin.setSafeOnClickListener {
                viewModel.registerByPhone()
            }
            ivBack.setSafeOnClickListener {
                viewModel.handleBackClick()
            }
            chkPolicy.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updatePolicyAgreementCheckboxState(isChecked)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.etPhoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        binding.etPhoneNumber.addTextChangedListener(countryCodeTextWatcher())
    }

    private fun countryCodeTextWatcher() = object : TextWatcher {

        private var isResetting = false

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(s: Editable) {
            if (isResetting) return

            if (!s.startsWith(CountryPhoneCode.PLUS_PREFIX)) {
                // Ensure countryCode is not removed
                isResetting = true
                binding.etPhoneNumber.setText(CountryPhoneCode.PLUS_PREFIX)
                binding.etPhoneNumber.setSelection(CountryPhoneCode.PLUS_PREFIX.length)
                isResetting = false
            }

            if (s.length == 2) {
                CountryPhoneCode.identifyCountry(s.toString())?.also { country ->
                    binding.etPhoneNumber.setText(country.internationalCode)
                    binding.etPhoneNumber.setSelection(country.internationalCode.length)
                }
            }
        }
    }

    companion object {
        /**
         * This key should be the same as in auth_nav_graph.xml
         */
        private const val IS_FIRST_LAUNCH_BUNDLE_KEY = "isFirstLaunch"

        fun createArgBundle(isFirstLaunch: Boolean): Bundle {
            return bundleOf(IS_FIRST_LAUNCH_BUNDLE_KEY to isFirstLaunch)
        }
    }
}