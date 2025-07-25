package com.example.client_volcal_baseline.network

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @POST("/create_task")
    suspend fun createTask(): CreateTaskResponse

    @POST("/start_task/{id}")
    suspend fun startTask(@Path("id") id: String): StartTaskResponse

    @GET("/query_task/{id}")
    suspend fun queryTask(@Path("id") id: String): QueryTaskResponse
}

data class CreateTaskResponse(
    val task_id: String,
    val upload_urls: Map<String, String>
)

data class StartTaskResponse(val started: Boolean)

data class HullItem(
    val id: String,
    val area: Double,
    val cut_volume: Double,
    val fill_volume: Double,
    val net_volume: Double,
    val image_key: String,
    val image_url: String,
)

data class QueryResults(
    val hulls: List<HullItem> = emptyList()
)

data class QueryTaskResponse(
    val status: String,
    val missing: List<String>? = null,
    val results: QueryResults? = null,
)
