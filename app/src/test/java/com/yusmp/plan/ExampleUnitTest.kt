package com.yusmp.plan

import com.yusmp.domain.common.model.ApiError
import com.yusmp.domain.common.model.CommonBackendFailure
import com.yusmp.plan.presentation.common.extentions.getFormattedThrowable
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_log_message_format_of_getFormattedThrowable() {
        val throwable = Throwable("test_log_message_format_of_logToFirebase")
        println(
            getFormattedThrowable(
                customKey = "test_log_message_format_of_logToFirebase",
                throwable = throwable
            )
        )
        println(getFormattedThrowable(customKey = null, throwable = throwable))

        val failure: Throwable = CommonBackendFailure(
            ApiError(
                extraMessage = "CommonBackendFailure extraMessage",
                code = 0,
                message = "CommonBackendFailure message",
                violations = emptyList(),
                key = "CommonBackendFailure key",
                email = "CommonBackendFailure email"
            )
        )
        println()
        println(getFormattedThrowable(null, failure))
    }
}