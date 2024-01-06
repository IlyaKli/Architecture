package com.yusmp.plan.presentation.common.extentions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.yusmp.plan.presentation.MainActivity
import com.yusmp.plan.presentation.common.utils.AppSnackBarUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Fragment.observeFlow(flow: Flow<T>, action: (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(action)
        }
    }
}

fun Fragment.showSnackBar(
    message: String,
    buttonTitle: String? = null,
    @DrawableRes startDrawableId: Int? = null,
    onClick: (() -> Unit)? = null,
    length: Int = Snackbar.LENGTH_LONG,
) {
    val viewGroup = view ?: return
    AppSnackBarUtils.showSnackBar(
        viewGroup = viewGroup,
        message = message,
        buttonText = buttonTitle,
        startDrawableId = startDrawableId,
        onClick = onClick,
        length = length
    )
}

fun Fragment.showSnackBar(
    @StringRes messageStringId: Int,
    @StringRes buttonTitleId: Int? = null,
    @DrawableRes startDrawableId: Int? = null,
    onClick: (() -> Unit)? = null,
    length: Int = Snackbar.LENGTH_LONG,
) {
    showSnackBar(
        message = getString(messageStringId),
        buttonTitle = buttonTitleId?.let { getString(it) },
        startDrawableId = startDrawableId,
        onClick = onClick,
        length = length,
    )
}

fun Fragment.openSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", requireContext().packageName, null)
    }.also(::startActivity)
}

fun Fragment.getDrawable(@DrawableRes drawableId: Int) =
    ContextCompat.getDrawable(requireContext(), drawableId)

fun Fragment.getColor(@ColorRes colorId: Int) = ContextCompat.getColor(requireContext(), colorId)

fun Fragment.openLinkInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}

// Should be called from onAttach method
fun Fragment.handleBackClick(onBackClick: () -> Unit) {
    requireActivity()
        .onBackPressedDispatcher
        .addCallback(
            /* owner = */ this,
            /* onBackPressedCallback = */ object : OnBackPressedCallback(/* enabled = */ true) {
                override fun handleOnBackPressed(): Unit = onBackClick.invoke()
            }
        )
}

fun Fragment.setHomeAsStartDestination() {
    (requireActivity() as? MainActivity)?.setHomeAsStartDestination()
}

fun Fragment.setStatusBarColor(@ColorRes colorId: Int) {
    requireActivity().window.statusBarColor = getColor(colorId)
}