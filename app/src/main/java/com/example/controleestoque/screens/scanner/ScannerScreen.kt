package com.example.controleestoque.screens.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.controleestoque.screens.barcode.BarcodeScannerLauncher

@Composable
fun ScannerScreen(
    onBarcodeScanned: (String) -> Unit
) {
    val context = LocalContext.current

    // Estado para mostrar/ocultar o diálogo de entrada manual
    val showManualEntryDialog = remember { mutableStateOf(false) }

    // Estado para armazenar o código de barras digitado manualmente
    val manualBarcodeText = remember { mutableStateOf("") }

    // Verificar permissão da câmera
    val hasCameraPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher para solicitar permissão da câmera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission.value = isGranted
    }

    // Launcher para o scanner de código de barras
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Processar o resultado do scanner
        val barcodeData = BarcodeScannerLauncher.getBarcodeScanResult(result)
        if (!barcodeData.isNullOrEmpty()) {
            onBarcodeScanned(barcodeData)
        }
    }

    // Diálogo para entrada manual de código
    if (showManualEntryDialog.value) {
        Dialog(onDismissRequest = { showManualEntryDialog.value = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Digite o Código de Barras",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = manualBarcodeText.value,
                        onValueChange = { manualBarcodeText.value = it },
                        label = { Text("Código de Barras") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showManualEntryDialog.value = false }
                        ) {
                            Text("Cancelar")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (manualBarcodeText.value.isNotBlank()) {
                                    onBarcodeScanned(manualBarcodeText.value)
                                    showManualEntryDialog.value = false
                                }
                            },
                            enabled = manualBarcodeText.value.isNotBlank()
                        ) {
                            Text("Confirmar")
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título e instruções
            Text(
                text = "Scanner de Código de Barras",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Escaneie o código de barras do produto para ver seus detalhes ou adicionar ao estoque",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = {
                            if (hasCameraPermission.value) {
                                BarcodeScannerLauncher.launch(context, scannerLauncher)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Escanear Código de Barras", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botão para entrada manual
                    OutlinedButton(
                        onClick = {
                            showManualEntryDialog.value = true
                            manualBarcodeText.value = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardAlt,
                            contentDescription = "Digitar código",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Digitar Código Manualmente", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}