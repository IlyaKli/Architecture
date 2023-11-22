package com.yusmp.domain

import com.yusmp.domain.common.model.ApiError
import com.yusmp.domain.common.model.CommonBackendFailure
import com.yusmp.domain.common.model.NoInternetFailure
import com.yusmp.domain.common.model.UnauthorizedFailure
import com.yusmp.domain.common.model.UnknownFailure
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
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

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun test_app_base_errors_classes() {
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
        val failure2: Throwable = UnknownFailure("UnknownFailure")
        val failure3: Throwable = NoInternetFailure("NoInternetFailure")
        val failure4: Throwable = UnauthorizedFailure(
            ApiError(
                extraMessage = "UnauthorizedFailure",
                code = 0,
                message = "UnauthorizedFailure message",
                violations = emptyList(),
                key = "UnauthorizedFailure key",
                email = "UnauthorizedFailure email"
            )
        )

        try {
            throw failure
        } catch (e: Exception) {
            println(e)
        }

        try {
            throw failure2
        } catch (e: Throwable) {
            println(e)
        }

        try {
            throw failure3
        } catch (e: Throwable) {
            println(e)
        }

        runBlocking {
            flow<Unit> { throw failure4 }.catch { e -> println(e) }.collect()
        }
    }
}