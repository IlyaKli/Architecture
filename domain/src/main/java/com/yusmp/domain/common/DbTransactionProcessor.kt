package com.yusmp.domain.common

interface DbTransactionProcessor {
    suspend fun runInTransaction(body: suspend () -> Unit)
}