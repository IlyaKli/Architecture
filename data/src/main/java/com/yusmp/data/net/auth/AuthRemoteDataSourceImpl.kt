package com.yusmp.data.net.auth

import com.yusmp.data.net.auth.model.AuthRequestBody
import com.yusmp.data.net.common.Mock
import com.yusmp.data.net.common.NetConverters.toDomain
import com.yusmp.domain.auth.AuthRemoteDataSource
import com.yusmp.domain.auth.model.AuthData
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    @Mock private val authApi: AuthApi
) : AuthRemoteDataSource {
    override suspend fun loginByPhone(email: String, password: String): AuthData {
        return authApi.authByEmail(AuthRequestBody(phone = email)).toDomain()
    }
}