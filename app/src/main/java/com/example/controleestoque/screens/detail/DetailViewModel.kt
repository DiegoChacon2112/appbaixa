package com.example.controleestoque.screens.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controleestoque.ProdutoRepository
import com.example.controleestoque.models.Produto
import com.example.controleestoque.repository.BaixaEstoqueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    // Estados para gerenciar a tela de detalhes
    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var produto by mutableStateOf<Produto?>(null)
        private set

    // Estado da UI para controle do processo de baixa
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val produtoRepository = ProdutoRepository()
    private val baixaRepository = BaixaEstoqueRepository()

    // Consulta o produto pelo código de barras
    fun consultarProduto(codigoBarras: String) {
        loading = true
        error = null
        viewModelScope.launch {
            try {
                when (val result = produtoRepository.consultarProduto(codigoBarras)) {
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

    // Realiza a baixa de estoque
    fun realizarBaixa(codigo: String, quantidade: Int) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = baixaRepository.baixarEstoque(codigo, quantidade)

                result.fold(
                    onSuccess = { response ->
                        if (response.sucess) {
                            _uiState.value = UiState.Success(response.message)
                        } else {
                            _uiState.value = UiState.Error(response.message)
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.value = UiState.Error("Erro na comunicação: ${throwable.message}")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Ocorreu um erro: ${e.message}")
            }
        }
    }

    // Reinicia os estados para uma nova consulta
    fun resetState() {
        loading = false
        error = null
        produto = null
        _uiState.value = UiState.Idle
    }

    // Classe para representar os estados da UI
    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }
}