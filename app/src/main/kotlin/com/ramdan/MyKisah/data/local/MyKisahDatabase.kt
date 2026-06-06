package com.ramdan.MyKisah.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PhotoLocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MyKisahDatabase : RoomDatabase() {
    abstract fun photoLocationDao(): PhotoLocationDao
}
