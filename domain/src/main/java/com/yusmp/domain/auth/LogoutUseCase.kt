package com.yusmp.domain.auth

import com.yusmp.domain.SuspendedUseCase
import com.yusmp.domain.dataStore.DataStoreSource
import javax.inject.Inject

interface LogoutUseCase : SuspendedUseCase<Unit, Unit>

@Suppress("LongParameterList")
class LogoutUseCaseImpl @Inject constructor(
    private val dataStore: DataStoreSource,
    private val authDbDataSource: AuthDbDataSource
) : LogoutUseCase {

    override suspend fun execute(param: Unit) {
        dataStore.clear()
        authDbDataSource.clearSessions()
    }
}
