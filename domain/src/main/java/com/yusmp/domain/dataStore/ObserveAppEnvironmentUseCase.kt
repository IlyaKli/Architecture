package com.yusmp.domain.dataStore

import com.yusmp.domain.LocalFlowUseCase
import com.yusmp.domain.dataStore.model.AppEnvironment
import javax.inject.Inject

interface ObserveAppEnvironmentUseCase : LocalFlowUseCase<Unit, AppEnvironment>

class ObserveAppEnvironmentUseCaseImpl @Inject constructor(
    private val dataStore: DataStoreSource
) : ObserveAppEnvironmentUseCase {
    override fun execute(param: Unit) = dataStore.appEnvironment
}