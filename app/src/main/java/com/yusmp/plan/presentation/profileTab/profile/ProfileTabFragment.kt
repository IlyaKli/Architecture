package com.yusmp.plan.presentation.profileTab.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.badoo.mvicore.modelWatcher
import com.yusmp.plan.R
import com.yusmp.plan.databinding.FragmentProfileTabBinding
import com.yusmp.plan.presentation.common.baseFragment.BaseFragment
import com.yusmp.plan.presentation.common.extentions.isLoading
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileTabFragment : BaseFragment<FragmentProfileTabBinding, ProfileUiState, ProfileEvent>() {
    override val viewModel: ProfileTabViewModel by viewModels()

    override val stateRenderer = modelWatcher {
        ProfileUiState::isLoading { isLoading ->
            binding.progressBar.isLoading = isLoading
        }
        ProfileUiState::phoneNumber { phoneNumber ->
            binding.tvPhoneNumber.text = phoneNumber
        }
        ProfileUiState::isUserAuthorized { isUserAuthorized ->
            binding.btnLogin.text = if (isUserAuthorized) {
                getString(R.string.authorization_phone_logout)
            } else {
                getString(R.string.authorization_phone_login)
            }
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentProfileTabBinding {
        return FragmentProfileTabBinding.inflate(inflater, container, false)
    }

    override fun ProfileEvent.handleEvent() {
        when (this) {
            is ProfileEvent.NavigateToAuthorization -> {
                findNavController().navigate(
                    ProfileTabFragmentDirections.actionProfileFragmentToAuthorizationNavGraph()
                )
            }
        }
    }


    override fun FragmentProfileTabBinding.setupViews() {
        btnLogin.setOnClickListener {
            viewModel.changeAuthorizationState()
        }
        btnTicTacCv.setOnClickListener {
            findNavController().navigate(ProfileTabFragmentDirections.actionProfileTabFragmentToTicTacToeFragment())
        }
        btnGantCv.setOnClickListener {
            findNavController().navigate(ProfileTabFragmentDirections.actionProfileTabFragmentToGantFragment())
        }
    }
}