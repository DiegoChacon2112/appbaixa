package com.example.controleestoque.models

data class Produto(
    val success: Boolean,
    val Descricao: String,
    val EstoqueAtual: Int
)

data class ProdutoRequest(
    val codigo: String
)