package com.yusmp.domain.dataStore

import com.yusmp.domain.SuspendedUseCase
import com.yusmp.domain.dataStore.model.AppEnvironment
import javax.inject.Inject

interface UpdateAppEnvironmentUseCase : SuspendedUseCase<AppEnvironment, Unit>

class UpdateAppEnvironmentUseCaseImpl @Inject constructor(
    private val dataStore: DataStoreSource
) : UpdateAppEnvironmentUseCase {
    override suspend fun execute(param: AppEnvironment) =
        dataStore.updateAppEnvironment(param)
}