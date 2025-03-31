package com.example.controleestoque.screens.barcode

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

object BarcodeScannerLauncher {

    /**
     * Inicia o scanner de código de barras com área de leitura reduzida
     */
    fun launch(context: Context, launcher: ActivityResultLauncher<Intent>) {
        // Verificar se o context é uma Activity e converter
        if (context !is Activity) {
            throw IllegalArgumentException("O contexto deve ser uma Activity para iniciar o scanner")
        }

        val activity = context as Activity

        // Usar IntentIntegrator mas configurando para uma área de escaneamento reduzida
        val integrator = IntentIntegrator(activity)
        integrator.apply {
            setDesiredBarcodeFormats(IntentIntegrator.CODE_128)
            setPrompt("Posicione o código de barras na área de leitura")
            setCameraId(0) // Câmera traseira
            setBeepEnabled(true)
            setBarcodeImageEnabled(false)
            setOrientationLocked(false)

            // Usar uma atividade de captura customizada
            setCaptureActivity(CustomScannerActivity::class.java)
        }

        // Iniciar o scanner usando o launcher
        val intent = integrator.createScanIntent()
        launcher.launch(intent)
    }

    /**
     * Obtém o resultado do scanner de código de barras
     */
    fun getBarcodeScanResult(result: ActivityResult): String? {
        // Verificar se o resultado é do ZXing
        val scanningResult = IntentIntegrator.parseActivityResult(
            result.resultCode,
            result.data
        )

        // Retornar o conteúdo do código de barras se disponível
        return scanningResult?.contents
    }

}

