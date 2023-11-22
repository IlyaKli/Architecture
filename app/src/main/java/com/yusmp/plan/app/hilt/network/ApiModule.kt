package com.yusmp.plan.app.hilt.network

import com.yusmp.data.net.auth.AuthApi
import com.yusmp.data.net.common.Network
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun bindAuthApi(retrofit: Retrofit): AuthApi = Network.getApi(retrofit)
}