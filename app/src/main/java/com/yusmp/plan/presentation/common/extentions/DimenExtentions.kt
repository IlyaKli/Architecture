package com.yusmp.plan.presentation.common.extentions

import android.content.res.Resources

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.dpToPx: Float
    get() = this * Resources.getSystem().displayMetrics.density
