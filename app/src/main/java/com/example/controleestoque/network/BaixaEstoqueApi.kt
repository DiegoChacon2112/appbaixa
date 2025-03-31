// BaixaEstoqueApi.kt
package com.example.controleestoque.network

import com.example.controleestoque.data.BaixaEstoqueRequest
import com.example.controleestoque.data.BaixaEstoqueResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BaixaEstoqueApi {
    @POST("VKBAIXAEST")
    suspend fun baixarEstoque(@Body request: BaixaEstoqueRequest): Response<BaixaEstoqueResponse>
}