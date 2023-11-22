package com.yusmp.data.db.auth

import com.yusmp.data.db.common.DbConverters.toDomain
import com.yusmp.data.db.common.DbConverters.toEntity
import com.yusmp.data.net.common.Mock
import com.yusmp.domain.auth.AuthDbDataSource
import com.yusmp.domain.auth.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthDbDataSourceImpl @Inject constructor(
    @Mock private val dao: SessionDao
) : AuthDbDataSource {

    override fun observeSession(): Flow<Session> =
        dao.observeLastSession().map { it.toDomain() }

    override fun getSession(): Session {
        return dao.getLastSession().toDomain()
    }

    override fun saveSession(session: Session) {
        dao.createSession(session.toEntity())
    }

    override fun clearSessions() {
        dao.clearSessions()
    }
}
