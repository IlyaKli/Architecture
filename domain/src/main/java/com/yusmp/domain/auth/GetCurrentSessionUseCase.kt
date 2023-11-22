package com.yusmp.domain.auth

import com.yusmp.domain.auth.model.Session
import com.yusmp.domain.SuspendedUseCase
import javax.inject.Inject

interface GetCurrentSessionUseCase : SuspendedUseCase<Unit, Session>

class GetCurrentSessionUseCaseImpl @Inject constructor(
    private val dataSource: AuthDbDataSource
) : GetCurrentSessionUseCase {

    override suspend fun execute(param: Unit): Session {
        return dataSource.getSession()
    }
}
