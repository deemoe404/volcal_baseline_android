package com.example.client_volcal_baseline.ui.csv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.client_volcal_baseline.network.CsvItem
import com.example.client_volcal_baseline.network.RetrofitProvider
import com.opencsv.CSVReaderBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStreamReader

class CsvViewModel(resultKey: String) : ViewModel() {

    private val _items = MutableStateFlow<List<CsvItem>>(emptyList())
    val items: StateFlow<List<CsvItem>> = _items.asStateFlow()

    private val _checked = MutableStateFlow<MutableList<Boolean>>(mutableListOf())
    val checked: StateFlow<MutableList<Boolean>> = _checked.asStateFlow()

    val total: StateFlow<Double> = combine(items, checked) { list, sel ->
        list.indices.filter { sel.getOrNull(it) == true }.sumOf { list[it].net }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    init { fetchCsv(resultKey) }

    private fun fetchCsv(key: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val presigned = RetrofitProvider.api.presign(key).url

            val stream = OkHttpClient().newCall(Request.Builder().url(presigned).build())
                .execute().body!!.byteStream()

            val reader = CSVReaderBuilder(InputStreamReader(stream))
                .withSkipLines(1).build()
            val parsed = reader.readAll().map { row ->
                CsvItem(
                    id = row[0], area = row[1].toDouble(), cut = row[2].toDouble(),
                    fill = row[3].toDouble(), net = row[4].toDouble(), imageKey = row[5]
                )
            }

            _items.value = parsed
            _checked.value = MutableList(parsed.size) { false }
        } catch (e: Exception) {
        }
    }

    fun toggle(idx: Int) = _checked.update { old ->
        old.toMutableList().also { it[idx] = !it[idx] }
    }
    fun selectAll() { _checked.value = MutableList(_items.value.size) { true } }
    fun clearAll()  { _checked.value = MutableList(_items.value.size) { false } }

    companion object {
        fun provideFactory(key: String) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                CsvViewModel(key) as T
        }
    }
}
