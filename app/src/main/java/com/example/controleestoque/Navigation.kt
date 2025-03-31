package com.example.controleestoque

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.controleestoque.screens.detail.DetailScreen
import com.example.controleestoque.screens.main.MainScreen
import com.example.controleestoque.screens.scanner.ScannerScreen
import com.example.controleestoque.screens.splash.SplashScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable(route = "splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable(route = "main") {
            // MainScreen com diálogo direto em vez de navegação para scanner
            MainScreen(
                onNavigateToDetail = { barcodeData ->
                    navController.navigate("detail/$barcodeData")
                }
            )
        }

        // Mantemos a rota do scanner para uso futuro ou outras entradas,
        // mas não navegamos para ela diretamente do MainScreen
        composable(route = "scanner") {
            ScannerScreen(
                onBarcodeScanned = { barcodeData ->
                    navController.navigate("detail/$barcodeData") {
                        popUpTo("scanner") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "detail/{barcodeData}",
            arguments = listOf(navArgument("barcodeData") { type = NavType.StringType })
        ) { backStackEntry ->
            val barcodeData = backStackEntry.arguments?.getString("barcodeData") ?: ""
            DetailScreen(
                barcodeData = barcodeData,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}