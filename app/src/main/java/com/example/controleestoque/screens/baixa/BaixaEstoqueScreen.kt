package com.example.controleestoque.screens.baixa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controleestoque.viewmodel.BaixaEstoqueUiState
import com.example.controleestoque.viewmodel.BaixaEstoqueViewModel
import kotlinx.coroutines.delay

@Composable
fun ConfirmarBaixaDialog(
    codigo: String,
    quantidade: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Baixa") },
        text = {
            Text("Você tem certeza de que deseja baixar o produto com o código $codigo e quantidade $quantidade?")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun BaixaEstoqueScreen(
    codigo: String,
    onNavigateToLeitura: () -> Unit,
    viewModel: BaixaEstoqueViewModel = viewModel()
) {
    var quantidadeText by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    // Efeito para navegar de volta após mostrar mensagem de erro
    LaunchedEffect(uiState) {
        if (uiState is BaixaEstoqueUiState.Error) {
            delay(2000) // Exibe a mensagem de erro por 2 segundos
            viewModel.resetState()
            onNavigateToLeitura()
        } else if (uiState is BaixaEstoqueUiState.Success) {
            delay(2000) // Exibe a mensagem de sucesso por 2 segundos
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Conteúdo principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Baixa de Estoque",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Código: $codigo",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = quantidadeText,
                onValueChange = { quantidadeText = it },
                label = { Text("Quantidade a baixar") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is BaixaEstoqueUiState.Loading // Desabilita durante o carregamento
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val quantidade = quantidadeText.toIntOrNull() ?: 0
                    if (quantidade > 0) {
                        showDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is BaixaEstoqueUiState.Loading && // Desabilita durante o carregamento
                        quantidadeText.toIntOrNull()?.let { it > 0 } ?: false // Ativa apenas se a quantidade for > 0
            ) {
                if (uiState is BaixaEstoqueUiState.Loading) {
                    // Mostra texto "Processando" quando estiver carregando
                    Text("Processando...")
                } else {
                    // Texto padrão do botão
                    Text("Realizar Baixa")
                }
            }

            // Indica visualmente se o botão está habilitado ou não
            if (quantidadeText.toIntOrNull()?.let { it <= 0 } ?: true && uiState !is BaixaEstoqueUiState.Loading) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Informe uma quantidade válida maior que zero",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Loading overlay
        if (uiState is BaixaEstoqueUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Processando...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Popup de confirmação
        if (showDialog) {
            ConfirmarBaixaDialog(
                codigo = codigo,
                quantidade = quantidadeText.toInt(),
                onConfirm = {
                    viewModel.baixarEstoque(codigo, quantidadeText.toInt())
                    showDialog = false
                },
                onDismiss = {
                    showDialog = false
                }
            )
        }

        // Mensagem de sucesso animada
        AnimatedVisibility(
            visible = uiState is BaixaEstoqueUiState.Success,
            enter = fadeIn(animationSpec = tween(500)) +
                    slideInVertically(animationSpec = tween(500), initialOffsetY = { -it }),
            exit = fadeOut(animationSpec = tween(500)) +
                    slideOutVertically(animationSpec = tween(500), targetOffsetY = { -it })
        ) {
            val successState = uiState as? BaixaEstoqueUiState.Success
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFDFF0D8) // Verde claro para sucesso
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Green
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = successState?.message ?: "Produto baixado com sucesso",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Mensagem de erro animada
        AnimatedVisibility(
            visible = uiState is BaixaEstoqueUiState.Error,
            enter = fadeIn(animationSpec = tween(500)) +
                    slideInVertically(animationSpec = tween(500), initialOffsetY = { -it }),
            exit = fadeOut(animationSpec = tween(500)) +
                    slideOutVertically(animationSpec = tween(500), targetOffsetY = { -it })
        ) {
            val errorState = uiState as? BaixaEstoqueUiState.Error
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8D7DA) // Vermelho claro para erro
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = Color(0xFFDC3545) // Vermelho escuro
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorState?.message ?: "Erro ao baixar o produto",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF721C24) // Cor de texto de erro
                    )
                }
            }
        }
    }
}