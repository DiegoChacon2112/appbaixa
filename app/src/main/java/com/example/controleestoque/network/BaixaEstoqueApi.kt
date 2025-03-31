package com.example.controleestoque.network

import com.example.controleestoque.data.BaixaEstoqueRequest  // Importação do pacote data
import com.example.controleestoque.data.BaixaEstoqueResponse // Importação do pacote data
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BaixaEstoqueApi {
    @POST("VKBAIXAEST")
    suspend fun baixarEstoque(@Body request: BaixaEstoqueRequest): Response<BaixaEstoqueResponse>
}