package com.yusmp.domain.auth

import com.yusmp.domain.auth.model.AuthData

interface AuthRemoteDataSource {
    suspend fun loginByPhone(email: String, password: String): AuthData
}