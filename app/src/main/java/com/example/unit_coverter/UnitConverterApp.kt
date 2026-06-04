package com.example.unit_coverter

import android.app.Application
import com.example.unit_coverter.domain.usecase.SyncCustomUnitsUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class UnitConverterApp : Application() {

    @Inject lateinit var syncCustomUnitsUseCase: SyncCustomUnitsUseCase

    // Application-scoped coroutine scope — cancelled when the process dies.
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        // Keep UnitRegistry's custom-unit overlay in sync with Room for the app's lifetime.
        appScope.launch { syncCustomUnitsUseCase() }
    }
}
