package com.example.client_volcal_baseline.network

data class TaskRequest(
    val pre_key: String,
    val post_key: String,
    val shp_key: String,
    val shx_key: String
)

data class CsvItem(
    val id: String,
    val area: Double,
    val cut: Double,
    val fill: Double,
    val net: Double,
    val imageKey: String
)

data class TaskResponse(val task_id: String)
