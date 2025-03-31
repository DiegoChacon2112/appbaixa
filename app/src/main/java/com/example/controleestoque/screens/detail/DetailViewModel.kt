package com.example.controleestoque.screens.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controleestoque.ProdutoRepository
import com.example.controleestoque.models.Produto
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    // Estados para gerenciar a tela de detalhes
    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var produto by mutableStateOf<Produto?>(null)
        private set

    private val repository = ProdutoRepository()

    // Consulta o produto pelo código de barras
    fun consultarProduto(codigoBarras: String) {
        loading = true
        error = null
        viewModelScope.launch {
            try {
                when (val result = repository.consultarProduto(codigoBarras)) {
                    is ProdutoRepository.Result.Success -> {
                        produto = result.produto
                        Log.d("DetailViewModel", "Produto consultado com sucesso: ${result.produto}")
                    }
                    is ProdutoRepository.Result.Error -> {
                        error = result.message
                        Log.e("DetailViewModel", "Erro ao consultar produto: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                error = "Erro inesperado: ${e.localizedMessage}"
                Log.e("DetailViewModel", "Exceção ao consultar produto", e)
            } finally {
                loading = false
            }
        }
    }

    // Reinicia os estados para uma nova consulta
    fun resetState() {
        loading = false
        error = null
        produto = null
    }
}