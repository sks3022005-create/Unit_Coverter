package com.example.unit_coverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.unit_coverter.data.local.prefs.UserPrefsDataStore
import com.example.unit_coverter.ui.navigation.AppBottomBar
import com.example.unit_coverter.ui.navigation.AppNavHost
import com.example.unit_coverter.ui.theme.UnitConverterTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPrefsDataStore: UserPrefsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDynamicColor by userPrefsDataStore.isDynamicColorEnabled
                .collectAsStateWithLifecycle(initialValue = false)
            val isForceDark by userPrefsDataStore.isDarkThemeForced
                .collectAsStateWithLifecycle(initialValue = false)

            UnitConverterTheme(
                darkTheme = isForceDark || isSystemInDarkTheme(),
                dynamicColor = isDynamicColor,
            ) {
                AppContent()
            }
        }
    }
}

@Composable
private fun AppContent() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { AppBottomBar(navController = navController) },
    ) { innerPadding ->
        // Only the top inset is padded here. Padding the NavHost by the full innerPadding
        // permanently reserves the bottom bar's height (~250px), and a screen that then
        // calls imePadding() has that subtracted a second time when the keyboard opens --
        // even though the bar is hidden behind the keyboard. That double subtraction is
        // what clipped the converter's result card down to its header. Screens consume the
        // bottom inset themselves (as trailing space that can scroll away), so the keyboard
        // and the bar never both claim the same pixels.
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
        )
    }
}
