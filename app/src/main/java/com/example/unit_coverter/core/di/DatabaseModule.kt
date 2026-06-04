package com.example.unit_coverter.core.di

import android.content.Context
import androidx.room.Room
import com.example.unit_coverter.data.local.db.AppDatabase
import com.example.unit_coverter.data.local.db.CustomUnitDao
import com.example.unit_coverter.data.local.db.FavoriteDao
import com.example.unit_coverter.data.local.db.HistoryDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "unit_converter.db",
        ).build()

    @Provides
    fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()

    @Provides
    fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun provideCustomUnitDao(db: AppDatabase): CustomUnitDao = db.customUnitDao()
}
