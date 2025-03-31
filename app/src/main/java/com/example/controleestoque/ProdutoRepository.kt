package com.example.controleestoque

import com.example.controleestoque.models.Produto
import com.example.controleestoque.models.ProdutoRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProdutoRepository {
    private val apiService = RetrofitClient.apiService

    sealed class Result {
        data class Success(val produto: Produto) : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun consultarProduto(codigoBarras: String): Result {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.consultarProduto(ProdutoRequest(codigoBarras))
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error("Erro: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Result.Error("Falha na conexão: ${e.localizedMessage}")
            }
        }
    }
}