package com.example.controleestoque

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.controleestoque.ui.theme.ControleEstoqueTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_ControleEstoque)
        // Instalar a splash screen antes de chamar setContent
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Opcional: você pode controlar quando a splash screen é fechada
        // Por exemplo, se precisar carregar dados antes:
        //splashScreen.setKeepOnScreenCondition { dataIsLoading }

        setContent {
            ControleEstoqueTheme(darkTheme = false) {
                val navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }
}