package com.yusmp.domain.auth

import com.yusmp.domain.auth.model.Session
import kotlinx.coroutines.flow.Flow

interface AuthDbDataSource {
    fun observeSession(): Flow<Session>
    fun getSession(): Session
    fun saveSession(session: Session)
    fun clearSessions()
}
