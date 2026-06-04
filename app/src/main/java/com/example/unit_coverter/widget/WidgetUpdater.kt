package com.example.unit_coverter.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun update(
        fromValue: String,
        fromUnit: String,
        toValue: String,
        toUnit: String,
    ) {
        val glanceIds = GlanceAppWidgetManager(context)
            .getGlanceIds(ConverterWidget::class.java)
        if (glanceIds.isEmpty()) return

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[WidgetKeys.FROM_VALUE] = fromValue
                    this[WidgetKeys.FROM_UNIT]  = fromUnit
                    this[WidgetKeys.TO_VALUE]   = toValue
                    this[WidgetKeys.TO_UNIT]    = toUnit
                }
            }
        }
        ConverterWidget().updateAll(context)
    }
}
