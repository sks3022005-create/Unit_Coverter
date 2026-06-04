package com.example.unit_coverter.widget

import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetKeys {
    val FROM_VALUE = stringPreferencesKey("widget_from_value")
    val FROM_UNIT  = stringPreferencesKey("widget_from_unit")
    val TO_VALUE   = stringPreferencesKey("widget_to_value")
    val TO_UNIT    = stringPreferencesKey("widget_to_unit")
}
