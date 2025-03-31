package com.example.controleestoque.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controleestoque.repository.BaixaEstoqueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BaixaEstoqueViewModel : ViewModel() {
    private val repository = BaixaEstoqueRepository()

    private val _uiState = MutableStateFlow<BaixaEstoqueUiState>(BaixaEstoqueUiState.Idle)
    val uiState: StateFlow<BaixaEstoqueUiState> = _uiState.asStateFlow()

    fun baixarEstoque(codigo: String, quantidade: Int) {
        _uiState.value = BaixaEstoqueUiState.Loading

        viewModelScope.launch {
            val result = repository.baixarEstoque(codigo, quantidade)

            result.fold(
                onSuccess = { response ->
                    if (response.sucess) {
                        _uiState.value = BaixaEstoqueUiState.Success(response.message)
                    } else {
                        _uiState.value = BaixaEstoqueUiState.Error(response.message)
                    }
                },
                onFailure = { throwable ->
                    _uiState.value = BaixaEstoqueUiState.Error("Erro na comunicação: ${throwable.message}")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = BaixaEstoqueUiState.Idle
    }
}

sealed class BaixaEstoqueUiState {
    object Idle : BaixaEstoqueUiState()
    object Loading : BaixaEstoqueUiState()
    data class Success(val message: String) : BaixaEstoqueUiState()
    data class Error(val message: String) : BaixaEstoqueUiState()
}