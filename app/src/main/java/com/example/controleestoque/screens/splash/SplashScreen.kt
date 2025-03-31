package com.example.controleestoque.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controleestoque.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Estados para controlar as animações
    val scale = remember { Animatable(0.3f) }
    val alpha = remember { Animatable(0f) }
    var startTextAnimation by remember { mutableStateOf(false) }

    // Animação de alpha para o texto
    val textAlpha by animateFloatAsState(
        targetValue = if (startTextAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "textAlpha"
    )

    // Efeito para executar as animações
    LaunchedEffect(key1 = true) {
        // Animar escala da logo (efeito de quicar)
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = EaseOutBounce
                )
            )
        }

        // Animar alpha da logo (fade in)
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = LinearEasing
                )
            )
        }

        // Pequeno atraso antes de mostrar o texto
        delay(400)
        startTextAnimation = true

        // Tempo total da splash screen
        delay(2500)
        onSplashFinished()
    }

    // IMPORTANTE: Usando Surface com cor de fundo vermelho explícito
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFCC0000) // Vermelho escuro
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo com animações
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo do aplicativo",
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale.value)
                        .alpha(alpha.value)
                )
            }

            // Título do aplicativo com efeito fade in
            Text(
                text = "Baixa de Estoque",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .alpha(textAlpha)
            )

            // Subtítulo
            Text(
                text = "Desenvolvido por MVK Gôndolas e Display - Versão 1.0",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .alpha(textAlpha)
            )
        }
    }
}