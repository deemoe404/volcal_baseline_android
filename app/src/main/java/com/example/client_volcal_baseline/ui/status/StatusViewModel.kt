package com.example.client_volcal_baseline.ui.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.client_volcal_baseline.network.RetrofitProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TaskState(
    val status: String = "unknown",
    val resultKey: String? = null,
    val loading: Boolean = false
)

class StatusViewModel(private val taskId: String) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()

    fun startPolling(lifecycle: Lifecycle) = viewModelScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (_state.value.status != "done") {
                fetchOnce()
                delay(30000)
            }
        }
    }

    fun manualRefresh() = viewModelScope.launch { fetchOnce() }

    private suspend fun fetchOnce() {
        try {
            _state.update { it.copy(loading = true) }
            val res = RetrofitProvider.api.checkStatus(taskId)
            _state.value = TaskState(
                status = res.status,
                resultKey = res.result,
                loading = false
            )
        } catch (e: Exception) {
            _state.update { it.copy(loading = false) }
        }
    }
}
