package com.yusmp.domain.common.model

import java.io.IOException

// it's critical to throw IOException inside Retrofit's pipeline or you'll get RuntimeException

data class NoInternetFailure(override val message: String? = null) : IOException(message)

data class CommonBackendFailure(val apiError: ApiError? = null) : IOException(apiError?.message)

data class UnauthorizedFailure(val apiError: ApiError? = null) : IOException(apiError?.message)

data class UnknownFailure(override val message: String? = null) : IOException(message)