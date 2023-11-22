package com.yusmp.plan.presentation.common.extentions

import android.view.Window
import androidx.core.view.WindowCompat
import com.yusmp.plan.presentation.common.models.StatusBarContentAppearanceMode

fun Window.setStatusBarContentColor(mode: StatusBarContentAppearanceMode) {
    WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = mode()
}