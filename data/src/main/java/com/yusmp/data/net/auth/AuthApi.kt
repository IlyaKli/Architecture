package com.yusmp.data.net.auth

import com.yusmp.data.net.auth.model.*
import com.yusmp.data.net.common.model.HeaderFlags
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers(HeaderFlags.NoToken.formattedHeader)
    @POST("authByEmail")
    suspend fun authByEmail(@Body body: AuthRequestBody): AuthDataResponse
}