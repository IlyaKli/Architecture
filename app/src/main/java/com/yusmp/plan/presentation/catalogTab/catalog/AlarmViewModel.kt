package com.yusmp.plan.presentation.catalogTab.catalog

import android.text.Editable
import com.yusmp.plan.presentation.alarm.AlarmItem
import com.yusmp.plan.presentation.alarm.AndroidAlarmScheduler
import com.yusmp.plan.presentation.common.baseFragment.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val androidAlarmScheduler: AndroidAlarmScheduler,
) : BaseViewModel<AlarmUiState, AlarmUiEvent>(AlarmUiState()) {

    override fun refresh(isUpdateAll: Boolean) = Unit

    private var alarmScheduleTime: Long = 0

    private var alarmScheduleMessage = ""

    fun updateAlarmScheduleTime(time: Editable?) {
        updateUiState { copy(isButtonReadyEnabled = !time.isNullOrEmpty() and alarmScheduleMessage.isNotEmpty()) }
        if (time.isNullOrEmpty()) return
        alarmScheduleTime = time.toString().toLong()
    }

    fun updateAlarmScheduleMessage(message: Editable?) {
        updateUiState { copy(isButtonReadyEnabled = !message.isNullOrEmpty() and (alarmScheduleTime > 0)) }
        message ?: return
        alarmScheduleMessage = message.toString()
    }

    fun startAlarmSchedule() {
        androidAlarmScheduler.schedule(
            AlarmItem(
                time = LocalDateTime.now().plusSeconds(alarmScheduleTime),
                message = alarmScheduleMessage
            )
        )
    }
}