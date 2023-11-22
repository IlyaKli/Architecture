package com.yusmp.plan.app.hilt.mock

import com.yusmp.data.db.auth.SessionDao
import com.yusmp.data.mockdata.AuthApiMockImpl
import com.yusmp.data.mockdata.SessionDaoMockImpl
import com.yusmp.data.net.auth.AuthApi
import com.yusmp.data.net.common.Mock
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface MockDataModule {

    @Binds
    @Mock
    @Singleton
    fun bindMockAuthApi(impl: AuthApiMockImpl): AuthApi

    @Binds
    @Mock
    @Singleton
    fun bindMockAuthDao(impl: SessionDaoMockImpl): SessionDao
}
