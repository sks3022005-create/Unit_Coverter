package com.example.unit_coverter.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Icon shown next to each category name.
 *
 * Keyed by [com.example.unit_coverter.core.registry.UnitCategory.id]; unknown ids
 * (including user-defined categories) fall back to a neutral glyph rather than
 * crashing or leaving a ragged gap in the chip row.
 */
fun categoryIcon(categoryId: String): ImageVector = when (categoryId) {
    "length" -> Icons.Default.Straighten
    "mass" -> Icons.Default.MonitorWeight
    "volume" -> Icons.Default.WaterDrop
    "temperature" -> Icons.Default.DeviceThermostat
    "area" -> Icons.Default.CropSquare
    "pressure" -> Icons.Default.Compress
    "energy" -> Icons.Default.Bolt
    "power" -> Icons.Default.Speed
    "force" -> Icons.Default.OpenWith
    "time" -> Icons.Default.Schedule
    "speed" -> Icons.Default.Speed
    "angle" -> Icons.Default.RotateRight
    "fuel_consumption" -> Icons.Default.LocalGasStation
    "data_storage" -> Icons.Default.Memory
    else -> Icons.Default.DataUsage
}
