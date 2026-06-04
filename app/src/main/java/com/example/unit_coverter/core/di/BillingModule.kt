package com.example.unit_coverter.core.di

import com.example.unit_coverter.billing.PremiumGate
import com.example.unit_coverter.billing.StubPremiumGate
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BillingModule {

    // For release builds: swap StubPremiumGate → PlayBillingGate
    @Binds
    @Singleton
    abstract fun bindPremiumGate(impl: StubPremiumGate): PremiumGate
}
