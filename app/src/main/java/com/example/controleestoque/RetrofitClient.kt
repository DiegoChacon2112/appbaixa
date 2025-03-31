package com.example.controleestoque

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClient {
    private const val BASE_URL = "https://192.168.0.251:8409/"
    private const val USERNAME = "admin"
    private const val PASSWORD = "msmvk"
    private const val TAG = "RetrofitClient"

    // Trust manager que aceita certificados autoassinados (apenas para desenvolvimento)
    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            Log.d(TAG, "checkClientTrusted: Certificate is trusted")
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            Log.d(TAG, "checkServerTrusted: Certificate is trusted")
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    // Configuração do OkHttpClient com interceptores para autenticação e logging
    private val okHttpClient: OkHttpClient by lazy {
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", Credentials.basic(USERNAME, PASSWORD))
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Configuração do Gson para lidar com possíveis inconsistências na resposta JSON
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Instância do Retrofit configurado
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // API Service para ser usado nas chamadas
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}