package com.yusmp.data.db.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yusmp.data.db.auth.SessionDao
import com.yusmp.data.db.auth.SessionEntity

@Database(
    version = 1,
    entities = [
        SessionEntity::class,
    ],
)
abstract class AppDatabase : RoomDatabase() {
    public abstract fun sessionDao(): SessionDao
}