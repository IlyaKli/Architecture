package com.yusmp.plan.app.hilt.db

import android.content.Context
import com.yusmp.data.db.common.AppDatabase
import com.yusmp.data.db.common.Database
import com.yusmp.data.db.common.DbTransactionProcessorImpl
import com.yusmp.domain.common.DbTransactionProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Database.build(context)

    @Provides
    fun provideDbTransactionProcessor(
        database: AppDatabase
    ): DbTransactionProcessor = DbTransactionProcessorImpl(database)
}