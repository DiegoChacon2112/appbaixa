// Caminho: app/src/main/java/com/example/controleestoque/data/BaixaEstoqueModels.kt
package com.example.controleestoque.data

// Modelo para o corpo da requisição
data class BaixaEstoqueRequest(
    val codigo: String,
    val quantidade: Int
)

// Modelo para a resposta da API
data class BaixaEstoqueResponse(
    val sucess: Boolean,
    val message: String
)