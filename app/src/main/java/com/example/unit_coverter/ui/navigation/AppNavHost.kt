package com.example.unit_coverter.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.unit_coverter.feature.calculator.CalculatorScreen
import com.example.unit_coverter.feature.converter.ConverterScreen
import com.example.unit_coverter.feature.cooking.CookingScreen
import com.example.unit_coverter.feature.customunit.AddEditCustomUnitScreen
import com.example.unit_coverter.feature.customunit.CustomUnitsScreen
import com.example.unit_coverter.feature.favorites.FavoritesScreen
import com.example.unit_coverter.feature.history.HistoryScreen
import com.example.unit_coverter.feature.search.SearchScreen
import com.example.unit_coverter.feature.settings.SettingsScreen
import kotlin.reflect.KClass

private data class BottomNavItem(
    val screen: Screen,
    val screenClass: KClass<out Screen>,
    val label: String,
    val icon: ImageVector,
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Converter, Screen.Converter::class, "Convert", Icons.Filled.Calculate),
    BottomNavItem(Screen.Calculator, Screen.Calculator::class, "Calc", Icons.Filled.Functions),
    BottomNavItem(Screen.Search, Screen.Search::class, "Search", Icons.Filled.Search),
    BottomNavItem(Screen.History, Screen.History::class, "History", Icons.Filled.History),
    BottomNavItem(Screen.Favorites, Screen.Favorites::class, "Favorites", Icons.Filled.Favorite),
    BottomNavItem(Screen.Settings, Screen.Settings::class, "Settings", Icons.Filled.Settings),
)

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    fun navigateToConverter(categoryId: String, fromUnitId: String? = null, toUnitId: String? = null) {
        navController.navigate(Screen.CategoryConverter(categoryId, fromUnitId, toUnitId))
    }

    fun navigateToCooking() {
        navController.navigate(Screen.Cooking)
    }

    fun navigateToCustomUnits() {
        navController.navigate(Screen.CustomUnits)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Converter,
        modifier = modifier,
    ) {
        composable<Screen.Converter> {
            ConverterScreen(
                onNavigateToCooking = ::navigateToCooking,
                onNavigateToCustomUnits = ::navigateToCustomUnits,
            )
        }

        composable<Screen.CategoryConverter> {
            ConverterScreen(
                onNavigateToCooking = ::navigateToCooking,
                onNavigateToCustomUnits = ::navigateToCustomUnits,
            )
        }

        composable<Screen.Calculator> { CalculatorScreen() }

        composable<Screen.Cooking> {
            CookingScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable<Screen.Search> {
            SearchScreen(onNavigateToConverter = ::navigateToConverter)
        }

        composable<Screen.History> {
            HistoryScreen(onNavigateToConverter = ::navigateToConverter)
        }

        composable<Screen.Favorites> {
            FavoritesScreen(onNavigateToConverter = ::navigateToConverter)
        }

        composable<Screen.Settings> { SettingsScreen() }

        composable<Screen.CustomUnits> {
            CustomUnitsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddEdit = { unitId ->
                    navController.navigate(Screen.AddEditCustomUnit(unitId))
                },
            )
        }

        composable<Screen.AddEditCustomUnit> {
            AddEditCustomUnitScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun AppBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(item.screenClass)
            } == true

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selected,
                onClick = {
                    navController.navigate(item.screen) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}
