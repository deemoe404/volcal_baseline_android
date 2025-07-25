package com.example.client_volcal_baseline.ui.csv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.client_volcal_baseline.network.HullItem
import com.example.client_volcal_baseline.network.RetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CsvViewModel(taskId: String) : ViewModel() {

    private val _items = MutableStateFlow<List<HullItem>>(emptyList())
    val items: StateFlow<List<HullItem>> = _items.asStateFlow()

    private val _checked = MutableStateFlow<MutableList<Boolean>>(mutableListOf())
    val checked: StateFlow<MutableList<Boolean>> = _checked.asStateFlow()

    val total: StateFlow<Double> = combine(items, checked) { list, sel ->
        list.indices.filter { sel.getOrNull(it) == true }.sumOf { list[it].net_volume }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    init { fetchResults(taskId) }

    private fun fetchResults(id: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val res = RetrofitProvider.api.queryTask(id)
            val parsed = res.results?.hulls ?: emptyList()
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
        fun provideFactory(taskId: String) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                CsvViewModel(taskId) as T
        }
    }
}
