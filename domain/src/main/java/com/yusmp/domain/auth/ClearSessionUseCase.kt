package com.yusmp.domain.auth

import com.yusmp.domain.SuspendedUseCase
import javax.inject.Inject

interface ClearSessionUseCase : SuspendedUseCase<Unit, Unit>

class ClearSessionUseCaseImpl @Inject constructor(
    private val authDbDataSource: AuthDbDataSource,
) : ClearSessionUseCase {

    override suspend fun execute(param: Unit) {
        authDbDataSource.clearSessions()
    }
}
