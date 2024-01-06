package com.yusmp.plan.presentation.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yusmp.plan.presentation.workers.makeStatusNotification
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @ApplicationContext
    @Inject
    lateinit var appContext: Context

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra(AndroidAlarmScheduler.ALARM_INTENT_EXTRA_MESSAGE_KEY)
            ?: return
        makeStatusNotification(message, appContext)
    }
}