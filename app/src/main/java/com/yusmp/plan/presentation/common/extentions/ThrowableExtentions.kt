package com.yusmp.plan.presentation.common.extentions

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

fun Throwable.logToFirebase(customKey: String? = null) {
    Firebase.crashlytics.log(getFormattedThrowable(customKey = customKey, throwable = this))
    kotlin.runCatching { }
}

fun getFormattedThrowable(customKey: String?, throwable: Throwable): String {
    return """
        ${if (customKey != null) "$customKey:" else ""}
        |message: ${throwable.message},
        |stackTrace: ${throwable.stackTraceToString()}.
    """.trimIndent()
}

fun <R> runCatchingLogToFirebase(block: () -> R): Result<R> = runCatching {
    block()
}.onFailure {
    it.logToFirebase()
}