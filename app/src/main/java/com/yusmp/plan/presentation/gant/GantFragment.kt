package com.yusmp.plan.presentation.gant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.badoo.mvicore.modelWatcher
import com.yusmp.plan.databinding.FragmentGantBinding
import com.yusmp.plan.presentation.common.baseFragment.BaseFragment
import com.yusmp.plan.presentation.customView.diagramGant.Task
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class GantFragment : BaseFragment<FragmentGantBinding, GantUiState, GantUiEvent>() {

    override val viewModel: GantViewModel by viewModels()

    override val stateRenderer = modelWatcher {
        GantUiState::isLoading {
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentGantBinding {
        return FragmentGantBinding.inflate(inflater, container, false)
    }

    override fun GantUiEvent.handleEvent() {
        when (this) {
            is GantUiEvent.NavigateToAuthorization -> {

            }
        }
    }


    override fun FragmentGantBinding.setupViews() {
        val now = LocalDate.now()
        cvGant.setTasks(
            listOf(
                Task(
                    name = "Task 1",
                    dateStart = now.minusMonths(1),
                    dateEnd = now
                ),
                Task(
                    name = "Task 2 long name",
                    dateStart = now.minusWeeks(2),
                    dateEnd = now.plusWeeks(1)
                ),
                Task(
                    name = "Task 3",
                    dateStart = now.minusMonths(2),
                    dateEnd = now.plusMonths(2)
                ),
                Task(
                    name = "Some Task 4",
                    dateStart = now.plusWeeks(2),
                    dateEnd = now.plusMonths(2).plusWeeks(1)
                ),
                Task(
                    name = "Task 5",
                    dateStart = now.minusMonths(2).minusWeeks(1),
                    dateEnd = now.plusWeeks(1)
                )
            )
        )
    }
}