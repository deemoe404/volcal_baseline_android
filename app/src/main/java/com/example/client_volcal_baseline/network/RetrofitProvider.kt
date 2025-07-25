package com.example.client_volcal_baseline.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitProvider {
    val api: Api by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.26:8000/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(Api::class.java)
    }
}
