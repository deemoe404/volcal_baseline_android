package com.example.client_volcal_baseline.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

interface Api {
    @POST("/process")
    suspend fun createTask(@Body body: TaskRequest): TaskResponse

    @GET("/status/{id}")
    suspend fun checkStatus(@Path("id") id: String): StatusResponse

    @Streaming
    @GET("/download/{key}")
    suspend fun downloadCsv(@Path("key") key: String): okhttp3.ResponseBody

    @Streaming
    @GET("/download/{key}")
    suspend fun downloadImage(@Path("key") key: String): okhttp3.ResponseBody

    @GET("/presign/{key}")
    suspend fun presign(@Path("key") key: String): PresignResp

    @Streaming
    @GET
    suspend fun downloadPresigned(@Url url: String): okhttp3.ResponseBody
}

data class PresignResp(val url: String)

data class StatusResponse(
    val status: String,
    val result: String?
)
