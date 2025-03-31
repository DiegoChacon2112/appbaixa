package com.example.controleestoque.network

import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.util.concurrent.TimeUnit

object RetrofitConfig {
    private const val BASE_URL = "https://192.168.0.251:8409/rest/"
    private const val USERNAME = "admin"
    private const val PASSWORD = "msmvk"

    // Trust manager para ignorar certificados SSL (em ambiente de desenvolvimento)
    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    private val client = OkHttpClient.Builder().apply {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        addInterceptor(logging)

        // Adiciona autenticação básica
        addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", Credentials.basic(USERNAME, PASSWORD))
                .build()
            chain.proceed(request)
        }

        // Configurando para ignorar SSL (apenas para desenvolvimento)
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        hostnameVerifier { _, _ -> true }

        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
    }.build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val baixaEstoqueApi: BaixaEstoqueApi = retrofit.create(BaixaEstoqueApi::class.java)
}

interface BaixaEstoqueApi {
    @POST("VKBAIXAEST")
    suspend fun baixarEstoque(@Body request: BaixaEstoqueRequest): Response<BaixaEstoqueResponse>
}

data class BaixaEstoqueRequest(
    val codigo: String,
    val quantidade: Int
)

data class BaixaEstoqueResponse(
    val sucess: Boolean,
    val message: String
)