package com.ramdan.MyKisah.di

import android.content.Context
import androidx.room.Room
import com.ramdan.MyKisah.data.local.MyKisahDatabase
import com.ramdan.MyKisah.data.local.PhotoLocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyKisahDatabase =
        Room.databaseBuilder(context, MyKisahDatabase::class.java, "mykisah.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDao(db: MyKisahDatabase): PhotoLocationDao = db.photoLocationDao()
}
