package com.yusmp.plan.app.hilt.utils

import android.content.Context
import com.yusmp.plan.presentation.alarm.AlarmScheduler
import com.yusmp.plan.presentation.alarm.AndroidAlarmScheduler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UtilsModule {

    companion object {
        @Provides
        fun getPhoneNumberUtil(@ApplicationContext context: Context): PhoneNumberUtil =
            PhoneNumberUtil.createInstance(context)
    }

    @Binds
    @Singleton
    fun getAndroidAlarmScheduler(impl: AndroidAlarmScheduler): AlarmScheduler
}