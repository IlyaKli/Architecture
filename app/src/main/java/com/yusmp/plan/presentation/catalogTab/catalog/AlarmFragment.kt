package com.yusmp.plan.presentation.catalogTab.catalog

import android.Manifest
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.badoo.mvicore.modelWatcher
import com.yusmp.plan.R
import com.yusmp.plan.databinding.FragmentCatalogTabBinding
import com.yusmp.plan.presentation.common.baseFragment.BaseFragment
import com.yusmp.plan.presentation.common.extentions.requestPermission
import com.yusmp.plan.presentation.common.utils.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmFragment : BaseFragment<FragmentCatalogTabBinding, AlarmUiState, AlarmUiEvent>() {

    override val viewModel: AlarmViewModel by viewModels()

    override val stateRenderer = modelWatcher {
        AlarmUiState::isButtonReadyEnabled { isButtonReadyEnabled ->
            binding.btnReady.isEnabled = isButtonReadyEnabled
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentCatalogTabBinding {
        return FragmentCatalogTabBinding.inflate(inflater, container, false)
    }

    override fun AlarmUiEvent.handleEvent() {}

    override fun FragmentCatalogTabBinding.setupViews() {
        etTime.doAfterTextChanged {
            viewModel.updateAlarmScheduleTime(it)
        }
        etMessage.doAfterTextChanged {
            viewModel.updateAlarmScheduleMessage(it)
        }
        btnReady.setSafeOnClickListener {
            requestPermission(
                onResult = { viewModel.startAlarmSchedule() },
                permissions = listOf(Manifest.permission.USE_EXACT_ALARM),
                dialogStringsId = listOf(
                    R.string.permission_instruction_alarm,
                    R.string.permission_instruction_agreement,
                    R.string.permission_instruction_cancel
                )
            )
        }
    }
}