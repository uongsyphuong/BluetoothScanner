package com.usphuong.bluetoothscanner.di

import com.apollographql.apollo.ApolloClient
import com.usphuong.bluetoothscanner.BluetoothScannerApplication
import com.usphuong.bluetoothscanner.BuildConfig
import com.usphuong.bluetoothscanner.data.remote.ApiService
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class RemoteModule {
    @Provides
    @Singleton
    @Named("forRetrofit")
    fun createOkHttpClientForRetrofit(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val networkConnectionInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                if (!BluetoothScannerApplication.instance.isNetworkConnected())
                    throw IOException("No internet connection")
                val request = chain.request()
                return chain.proceed(request.newBuilder().build())
            }

        }
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(networkConnectionInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("forApollo")
    fun createOkHttpClientForApollo(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val networkConnectionInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                if (!BluetoothScannerApplication.instance.isNetworkConnected())
                    throw IOException("No internet connection")
                val request = chain.request()
                return chain.proceed(request.newBuilder().build())
            }

        }
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(networkConnectionInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(original.method,
                    original.body)
                builder.addHeader("Authorization", "Bearer " + BuildConfig.AUTH_TOKEN)
                chain.proceed(builder.build())
            }
            .build()
    }

    @Provides
    @Singleton
    fun createService(@Named("forRetrofit") okHttpClient: OkHttpClient): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun createApolloClient(@Named("forApollo") okHttpClient: OkHttpClient): ApolloClient {
        val BASE_URL = "https://api.github.com/graphql"
        return ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .build()
    }

}

