package com.yusmp.plan.presentation.ticTacToe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.badoo.mvicore.modelWatcher
import com.yusmp.plan.databinding.FragmentTicTacToeBinding
import com.yusmp.plan.presentation.common.baseFragment.BaseFragment
import com.yusmp.plan.presentation.customView.ticTacToe.CellType
import com.yusmp.plan.presentation.customView.ticTacToe.TicTacToeField
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TicTacToeFragment :
    BaseFragment<FragmentTicTacToeBinding, TicTacToeUiState, TicTacToeUiEvent>() {
    override val viewModel: TicTacToeViewModel by viewModels()

    //  Для теста
    private var isFirstPlayer = true

    override val stateRenderer = modelWatcher {
        TicTacToeUiState::isLoading {
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentTicTacToeBinding {
        return FragmentTicTacToeBinding.inflate(inflater, container, false)
    }

    override fun TicTacToeUiEvent.handleEvent() {
        when (this) {
            is TicTacToeUiEvent.NavigateToAuthorization -> {

            }
        }
    }


    override fun FragmentTicTacToeBinding.setupViews() {
        cvTicTacToe.ticTacToeField = TicTacToeField(7, 9)
        cvTicTacToe.onCellClickListener = { row, column, field ->
            if (field.getCell(row, column) == CellType.EMPTY)
                field.setCell(row, column, if (isFirstPlayer) CellType.CROSS else CellType.ZERO)
            isFirstPlayer = !isFirstPlayer
        }
    }
}