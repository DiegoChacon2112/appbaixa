package com.example.controleestoque.repository

import com.example.controleestoque.network.BaixaEstoqueApi
import com.example.controleestoque.network.BaixaEstoqueRequest
import com.example.controleestoque.network.BaixaEstoqueResponse
import com.example.controleestoque.network.RetrofitConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BaixaEstoqueRepository {
    private val api: BaixaEstoqueApi = RetrofitConfig.baixaEstoqueApi

    suspend fun baixarEstoque(codigo: String, quantidade: Int): Result<BaixaEstoqueResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = BaixaEstoqueRequest(codigo, quantidade)
                val response = api.baixarEstoque(request)

                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Resposta vazia"))
                } else {
                    Result.failure(Exception("Erro: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}