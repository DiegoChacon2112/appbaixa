package com.example.controleestoque.models

/**
 * Classe para deserialização da resposta da API
 */
data class ProdutoResponse(
    val success: Boolean = false,
    val message: String? = null,
    val data: Produto? = null
)