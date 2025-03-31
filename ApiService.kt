package com.example.controleestoque

import com.example.controleestoque.models.Produto
import com.example.controleestoque.models.ProdutoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("rest/VKBAIXAPRD")
    fun consultarProduto(@Body request: ProdutoRequest): Call<Produto>
}