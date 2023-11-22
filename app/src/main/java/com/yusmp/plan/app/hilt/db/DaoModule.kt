package com.yusmp.plan.app.hilt.db

import com.yusmp.data.db.auth.SessionDao
import com.yusmp.data.db.common.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    @Singleton
    fun bindSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
}