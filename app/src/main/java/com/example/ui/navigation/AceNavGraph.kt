package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.AceViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

sealed class AceBottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    object Dashboard : AceBottomNavItem("dashboard", "Home", Icons.Default.Home, "tab_dashboard")
    object Glossary : AceBottomNavItem("glossary", "Search", Icons.Default.Search, "tab_glossary")
    object Modules : AceBottomNavItem("modules", "Modules", Icons.Default.MenuBook, "tab_modules")
    object ComputeLab : AceBottomNavItem("compute_lab", "Compute", Icons.Default.Dns, "tab_compute")
    object StorageLab : AceBottomNavItem("storage_lab", "Storage", Icons.Default.Storage, "tab_storage")
    object Quiz : AceBottomNavItem("quiz", "Quiz", Icons.Default.Quiz, "tab_quiz")
    object Bookmarks : AceBottomNavItem("bookmarks", "Saved", Icons.Default.Bookmark, "tab_bookmarks")
}

@Composable
fun AceMainAppScreen(viewModel: AceViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomItems = listOf(
        AceBottomNavItem.Dashboard,
        AceBottomNavItem.Glossary,
        AceBottomNavItem.Modules,
        AceBottomNavItem.ComputeLab,
        AceBottomNavItem.StorageLab,
        AceBottomNavItem.Quiz,
        AceBottomNavItem.Bookmarks
    )

    val showBottomBar = currentRoute in bottomItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = EditorialSurface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    bottomItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = EditorialPrimaryContainer,
                                selectedIconColor = EditorialPrimaryDark,
                                selectedTextColor = EditorialPrimaryDark,
                                unselectedIconColor = EditorialTextSecondary,
                                unselectedTextColor = EditorialTextSecondary
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AceBottomNavItem.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AceBottomNavItem.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToModules = { navController.navigate(AceBottomNavItem.Modules.route) },
                    onNavigateToLesson = { lessonId -> navController.navigate("lesson_detail/$lessonId") },
                    onNavigateToComputeSim = { navController.navigate(AceBottomNavItem.ComputeLab.route) },
                    onNavigateToStorageSim = { navController.navigate(AceBottomNavItem.StorageLab.route) },
                    onNavigateToQuiz = { navController.navigate(AceBottomNavItem.Quiz.route) },
                    onNavigateToGlossary = { navController.navigate(AceBottomNavItem.Glossary.route) }
                )
            }

            composable(AceBottomNavItem.Glossary.route) {
                GlossaryScreen(
                    viewModel = viewModel,
                    onNavigateBack = null
                )
            }

            composable(AceBottomNavItem.Modules.route) {
                ModulesScreen(
                    viewModel = viewModel,
                    onNavigateToLesson = { lessonId -> navController.navigate("lesson_detail/$lessonId") }
                )
            }

            composable(
                route = "lesson_detail/{lessonId}",
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) { backStack ->
                val lessonId = backStack.arguments?.getString("lessonId") ?: ""
                LessonDetailScreen(
                    lessonId = lessonId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(AceBottomNavItem.ComputeLab.route) {
                ComputeSimulatorScreen(viewModel = viewModel)
            }

            composable(AceBottomNavItem.StorageLab.route) {
                StorageSimulatorScreen(viewModel = viewModel)
            }

            composable(AceBottomNavItem.Quiz.route) {
                QuizScreen(viewModel = viewModel)
            }

            composable(AceBottomNavItem.Bookmarks.route) {
                BookmarksScreen(viewModel = viewModel)
            }
        }
    }
}
