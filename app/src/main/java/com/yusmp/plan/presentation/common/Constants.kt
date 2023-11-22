package com.yusmp.plan.presentation.common

// region Notification Channel constants

// Name of Notification Channel for plan notifications of background work
@JvmField
val PLAN_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Plan WorkManager Notifications"
const val PLAN_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
@JvmField
val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
const val CHANNEL_ID = "PLAN_NOTIFICATION"
const val NOTIFICATION_ID = 1
// endregion

// region The name of the image manipulation work
const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"
// endregion

// region Other keys
const val OUTPUT_PATH = "blur_filter_outputs"
const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
const val TAG_OUTPUT = "OUTPUT"
// endregion

const val DELAY_TIME_MILLIS: Long = 6000