package com.extremesudoku.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.extremesudoku.presentation.auth.AuthScreen
import com.extremesudoku.presentation.game.GameScreen
import com.extremesudoku.presentation.home.HomeScreen
import com.extremesudoku.presentation.leaderboard.LeaderboardScreen
import com.extremesudoku.presentation.profile.ProfileScreen
import com.extremesudoku.presentation.settings.SettingsScreen
import com.extremesudoku.presentation.pvp.PvpModeSelectionScreen
import com.extremesudoku.presentation.pvp.lobby.PvpLobbyScreen
import com.extremesudoku.presentation.pvp.game.PvpBlindRaceScreen
import com.extremesudoku.presentation.pvp.game.PvpLiveBattleScreen
import com.extremesudoku.presentation.pvp.result.PvpResultScreen
import com.extremesudoku.data.models.pvp.PvpMode
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun SudokuNavigation() {
    val navController = rememberNavController()
    val auth = Firebase.auth
    
    // Anonymous user bile olsa Firebase'de auth olmuş demektir
    // Auth screen'e sadece hiç user yoksa git
    val startDestination = if (auth.currentUser != null) {
        Screen.Home.route
    } else {
        Screen.Auth.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToNewGame = { sudokuId ->
                    navController.navigate(Screen.NewGame.createRoute(sudokuId))
                },
                onNavigateToContinueGame = { gameId ->
                    navController.navigate(Screen.ContinueGame.createRoute(gameId))
                },
                onNavigateToPvp = {
                    navController.navigate(Screen.PvpModeSelection.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToLeaderboard = {
                    navController.navigate(Screen.Leaderboard.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // YENİ OYUN ROUTE'U - Unique gameId oluşturulacak
        composable(
            route = Screen.NewGame.route,
            arguments = listOf(
                navArgument("sudokuId") {
                    type = NavType.StringType
                }
            )
        ) {
            GameScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // DEVAM EDEN OYUN ROUTE'U - Kayıtlı gameId kullanılacak
        composable(
            route = Screen.ContinueGame.route,
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.StringType
                }
            )
        ) {
            GameScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.PvpModeSelection.route) {
            PvpModeSelectionScreen(
                onModeSelected = { mode ->
                    navController.navigate(Screen.PvpLobby.createRoute(mode))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.PvpLobby.route,
            arguments = listOf(
                navArgument("mode") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val modeString = backStackEntry.arguments?.getString("mode") ?: "BLIND_RACE"
            val mode = when (modeString) {
                "LIVE_BATTLE" -> PvpMode.LIVE_BATTLE
                else -> PvpMode.BLIND_RACE
            }
            
            PvpLobbyScreen(
                mode = mode,
                onNavigateToGame = { matchId ->
                    // Mode'a göre farklı screen'e yönlendir
                    val route = if (mode == PvpMode.BLIND_RACE) {
                        Screen.PvpBlindRaceGame.createRoute(matchId)
                    } else {
                        Screen.PvpLiveBattleGame.createRoute(matchId)
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Blind Race Game Screen
        composable(
            route = Screen.PvpBlindRaceGame.route,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            
            PvpBlindRaceScreen(
                matchId = matchId,
                onNavigateToResult = {
                    navController.navigate(Screen.PvpResult.createRoute(matchId)) {
                        popUpTo(Screen.PvpBlindRaceGame.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }
        
        // Live Battle Game Screen
        composable(
            route = Screen.PvpLiveBattleGame.route,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            
            PvpLiveBattleScreen(
                matchId = matchId,
                onNavigateToResult = {
                    navController.navigate(Screen.PvpResult.createRoute(matchId)) {
                        popUpTo(Screen.PvpLiveBattleGame.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }
        
        composable(
            route = Screen.PvpResult.route,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            
            PvpResultScreen(
                matchId = matchId,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onPlayAgain = {
                    navController.navigate(Screen.PvpModeSelection.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Home : Screen("home")
    
    // YENİ OYUN - sudokuId ile yeni gameId oluşturulacak
    object NewGame : Screen("game/new/{sudokuId}") {
        fun createRoute(sudokuId: String) = "game/new/$sudokuId"
    }
    
    // DEVAM EDEN OYUN - Kayıtlı gameId kullanılacak
    object ContinueGame : Screen("game/continue/{gameId}") {
        fun createRoute(gameId: String) = "game/continue/$gameId"
    }
    
    object Profile : Screen("profile")
    object Leaderboard : Screen("leaderboard")
    object Settings : Screen("settings")
    object PvpModeSelection : Screen("pvp/mode-selection")
    object PvpLobby : Screen("pvp/lobby/{mode}") {
        fun createRoute(mode: String) = "pvp/lobby/$mode"
    }
    object PvpBlindRaceGame : Screen("pvp/game/blind-race/{matchId}") {
        fun createRoute(matchId: String) = "pvp/game/blind-race/$matchId"
    }
    object PvpLiveBattleGame : Screen("pvp/game/live-battle/{matchId}") {
        fun createRoute(matchId: String) = "pvp/game/live-battle/$matchId"
    }
    object PvpResult : Screen("pvp/result/{matchId}") {
        fun createRoute(matchId: String) = "pvp/result/$matchId"
    }
}
