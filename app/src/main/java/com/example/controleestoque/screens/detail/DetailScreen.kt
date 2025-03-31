package com.example.controleestoque.screens.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    barcodeData: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    // Efetuar a consulta na API quando a tela for aberta
    LaunchedEffect(barcodeData) {
        viewModel.consultarProduto(barcodeData)
    }

    // Estados para o diálogo de confirmação de baixa e quantidade
    val showConfirmDialog = remember { mutableStateOf(false) }
    var quantidadeBaixa by remember { mutableStateOf("1") } // Valor padrão de 1

    // Data e hora atual formatada
    val dataHoraAtual = remember {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        LocalDateTime.now().format(formatter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Produto", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            // Estado de carregamento
            AnimatedVisibility(
                visible = viewModel.loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Buscando informações do produto...", color = Color.Gray)
                }
            }

            // Estado de erro
            AnimatedVisibility(
                visible = viewModel.error != null && !viewModel.loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Erro",
                        tint = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Erro ao buscar informações",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = viewModel.error ?: "Erro desconhecido",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = {
                        viewModel.consultarProduto(barcodeData)
                    }) {
                        Text("Tentar Novamente")
                    }
                }
            }

            // Estado de produto não encontrado (com base na resposta da API)
            AnimatedVisibility(
                visible = viewModel.produto != null &&
                        !viewModel.loading &&
                        viewModel.produto?.Descricao == "Produto nao encontrado",
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Produto não encontrado",
                        tint = Color(0xFFF57C00), // Laranja para indicar atenção
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Produto não encontrado",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF57C00) // Laranja para indicar atenção
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "O código de barras $barcodeData não corresponde a nenhum produto cadastrado.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = onNavigateBack) {
                        Text("Voltar para Scanner")
                    }
                }
            }

            // Estado de sucesso - Produto encontrado
            AnimatedVisibility(
                visible = viewModel.produto != null &&
                        !viewModel.loading &&
                        viewModel.produto?.Descricao != "Produto nao encontrado",
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Código de barras escaneado
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Código Escaneado:",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = barcodeData,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Informações do produto
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Informações do Produto",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp),
                                color = MaterialTheme.colorScheme.primary
                            )

                            viewModel.produto?.let { produto ->
                                // Descrição
                                Text(text = "Descrição:", fontWeight = FontWeight.Medium, color = Color.Gray)
                                Text(
                                    text = produto.Descricao,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                                // Estoque atual
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Estoque Atual:", fontWeight = FontWeight.Medium, color = Color.Gray)
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (produto.EstoqueAtual > 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = produto.EstoqueAtual.toString(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }

                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                                // Campo para quantidade a ser baixada COM botões de + e -
                                Text(text = "Quantidade a baixar:", fontWeight = FontWeight.Medium, color = Color.Gray)

                                // Nova interface com botões de + e -
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Botão de decremento
                                    FilledIconButton(
                                        onClick = {
                                            val currentValue = quantidadeBaixa.toIntOrNull() ?: 0
                                            if (currentValue > 1) {
                                                quantidadeBaixa = (currentValue - 1).toString()
                                            }
                                        },
                                        modifier = Modifier.size(48.dp),
                                        enabled = (quantidadeBaixa.toIntOrNull() ?: 0) > 1
                                    ) {
                                        Text(
                                            text = "−",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Campo de texto para quantidade
                                    OutlinedTextField(
                                        value = quantidadeBaixa,
                                        onValueChange = {
                                            // Validar que seja apenas números positivos
                                            val filtered = it.filter { char -> char.isDigit() }
                                            if (filtered.isEmpty()) {
                                                quantidadeBaixa = ""
                                            } else {
                                                val num = filtered.toIntOrNull() ?: 1
                                                if (num > 0) quantidadeBaixa = num.toString()
                                            }
                                        },
                                        modifier = Modifier
                                            .width(120.dp)
                                            .padding(horizontal = 8.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            textAlign = TextAlign.Center,
                                            fontSize = 18.sp
                                        )
                                    )

                                    // Botão de incremento
                                    FilledIconButton(
                                        onClick = {
                                            val currentValue = quantidadeBaixa.toIntOrNull() ?: 0
                                            val maxValue = viewModel.produto?.EstoqueAtual ?: Int.MAX_VALUE
                                            if (currentValue < maxValue) {
                                                quantidadeBaixa = (currentValue + 1).toString()
                                            }
                                        },
                                        modifier = Modifier.size(48.dp),
                                        enabled = (quantidadeBaixa.toIntOrNull() ?: 0) < (viewModel.produto?.EstoqueAtual ?: Int.MAX_VALUE)
                                    ) {
                                        Text(
                                            text = "+",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Botões de ação
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Botão para escanear outro código
                        Button(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            )
                        ) {
                            Text(
                                text = "Voltar",
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Botão para realizar baixa
                        Button(
                            onClick = {
                                // Mostrar diálogo de confirmação
                                showConfirmDialog.value = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            ),
                            enabled = quantidadeBaixa.isNotEmpty() &&
                                    (quantidadeBaixa.toIntOrNull() ?: 0) > 0 &&
                                    (quantidadeBaixa.toIntOrNull() ?: 0) <= (viewModel.produto?.EstoqueAtual ?: 0)
                        ) {
                            Text(
                                text = "Realizar Baixa",
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Diálogo de confirmação para a baixa de estoque
            if (showConfirmDialog.value) {
                val quantidade = quantidadeBaixa.toIntOrNull() ?: 1
                val produto = viewModel.produto

                AlertDialog(
                    onDismissRequest = { showConfirmDialog.value = false },
                    title = { Text("Confirmar Baixa") },
                    text = {
                        Column {
                            Text("Deseja realizar a baixa de:")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$quantidade unidade(s)",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Produto: ${produto?.Descricao}")
                            Text(text = "Código: $barcodeData")
                            Text(text = "Data: $dataHoraAtual")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Aqui você implementaria a lógica para realizar a baixa no sistema
                                // Por exemplo, chamar um método do viewModel
                                // viewModel.realizarBaixa(barcodeData, quantidade, dataHoraAtual)
                                showConfirmDialog.value = false
                                // Pode adicionar lógica para mostrar um toast de sucesso
                            }
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showConfirmDialog.value = false
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}