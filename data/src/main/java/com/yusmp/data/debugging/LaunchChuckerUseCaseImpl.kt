package com.yusmp.data.debugging

import android.content.Context
import com.chuckerteam.chucker.api.Chucker
import com.yusmp.domain.debugging.LaunchChuckerUseCase
import javax.inject.Inject

class LaunchChuckerUseCaseImpl @Inject constructor(
    private val context: Context
) : LaunchChuckerUseCase {

    override fun invoke() {
        context.startActivity(Chucker.getLaunchIntent(context))
    }
}