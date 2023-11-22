package com.yusmp.domain.dataStore


import com.yusmp.domain.SuspendedUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface GetIsFirstLaunchAndSetFalseUseCase : SuspendedUseCase<Unit, Boolean>

class GetIsFirstLaunchAndSetFalseUseCaseImpl @Inject constructor(
    private val dataStore: DataStoreSource
) : GetIsFirstLaunchAndSetFalseUseCase {
    override suspend fun execute(param: Unit): Boolean {
        val isFirstLaunch = dataStore.isFirstLaunch.first()
        if (isFirstLaunch) dataStore.updateIsFirstLaunch(isFirstLaunch = false)
        return isFirstLaunch
    }
}