package com.example.unit_coverter.core.di

import com.example.unit_coverter.data.repository.CustomUnitRepositoryImpl
import com.example.unit_coverter.data.repository.FavoriteRepositoryImpl
import com.example.unit_coverter.data.repository.HistoryRepositoryImpl
import com.example.unit_coverter.domain.repository.CustomUnitRepository
import com.example.unit_coverter.domain.repository.FavoriteRepository
import com.example.unit_coverter.domain.repository.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindCustomUnitRepository(impl: CustomUnitRepositoryImpl): CustomUnitRepository
}
