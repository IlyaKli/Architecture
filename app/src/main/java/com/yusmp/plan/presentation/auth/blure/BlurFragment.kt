package com.yusmp.plan.presentation.auth.blure

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.badoo.mvicore.modelWatcher
import com.yusmp.plan.R
import com.yusmp.plan.databinding.FragmentPhoneAuthBinding
import com.yusmp.plan.presentation.common.baseFragment.BaseFragment
import com.yusmp.plan.presentation.common.extentions.requestPermission
import com.yusmp.plan.presentation.common.utils.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlurFragment : BaseFragment<FragmentPhoneAuthBinding, BlurUiState, BlurUiEvent>() {

    override val viewModel: BlurViewModel by viewModels()
    private val blurLevel: Int
        get() =
            when (binding.rgBlurLevel.checkedRadioButtonId) {
                R.id.radio_blur_lv_1 -> 1
                R.id.radio_blur_lv_2 -> 2
                R.id.radio_blur_lv_3 -> 3
                else -> 1
            }

    override val stateRenderer = modelWatcher {
        BlurUiState::workState binding@{ workState ->
            when (workState) {
                WorkState.NONE -> return@binding

                WorkState.PROGRESS -> {
                    with(binding) {
                        btnCancel.isVisible = true
                        btnGo.isVisible = false
                        btnSeeFile.isVisible = false
                    }
                }

                WorkState.FINISHED -> {
                    with(binding) {
                        btnCancel.isVisible = false
                        btnGo.isVisible = true
                        btnSeeFile.isVisible = true
                    }
                }
            }
        }

        BlurUiState::isLoading { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPhoneAuthBinding {
        return FragmentPhoneAuthBinding.inflate(inflater, container, false)
    }

    override fun BlurUiEvent.handleEvent() {}

    override fun FragmentPhoneAuthBinding.setupViews() {
        btnGo.setSafeOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermission(
                    onResult = { viewModel.applyBlur(blurLevel) },
                    permissions = listOf(Manifest.permission.POST_NOTIFICATIONS),
                    dialogStringsId = listOf(
                        R.string.permission_instruction,
                        R.string.permission_instruction_agreement,
                        R.string.permission_instruction_cancel
                    )
                )
            } else {
                viewModel.applyBlur(blurLevel)
            }
        }
        btnSeeFile.setSafeOnClickListener {
            viewModel.outputUri?.let { currentUri ->
                val actionView = Intent(Intent.ACTION_VIEW, currentUri)
                actionView.resolveActivity(requireActivity().packageManager)?.run {
                    startActivity(actionView)
                }
            }
        }
        btnCancel.setSafeOnClickListener {
            viewModel.cancelWork()
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