package com.example.client_volcal_baseline.ui.upload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import com.example.client_volcal_baseline.network.RetrofitProvider
import com.example.client_volcal_baseline.util.copyToTemp
import com.example.client_volcal_baseline.util.displayName
import io.tus.android.client.TusPreferencesURLStore
import io.tus.java.client.TusClient
import io.tus.java.client.TusUpload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

data class Slot(val uri: Uri? = null, val size: Long? = null)

private const val SLOT_COUNT = 4

class UploadViewModel(private val ctx: Context) : ViewModel() {

    private val prefs = ctx.getSharedPreferences("tus", 0)

    private val client: TusClient by lazy {
        TusClient().apply {
            enableResuming(TusPreferencesURLStore(prefs))
            enableRemoveFingerprintOnSuccess()
        }
    }

    private val _slots = MutableStateFlow(List(SLOT_COUNT) { Slot() })
    val slots: StateFlow<List<Slot>> = _slots.asStateFlow()

    private val _progress = MutableStateFlow<Float?>(null)
    val progress: StateFlow<Float?> = _progress.asStateFlow()

    val ready: StateFlow<Boolean> = combine(slots, progress) { s, p ->
        s.all { it.uri != null } && p == null
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun setUri(index: Int, uri: Uri, size: Long) {
        _slots.update { list ->
            list.toMutableList().also { it[index] = it[index].copy(uri = uri, size = size) }
        }
    }

    private suspend fun uploadOne(uri: Uri, slotIdx: Int, url: String) =
        withContext(Dispatchers.IO) {

            val file = uri.copyToTemp(ctx)

            val fileName = uri.displayName(ctx)
            val upload = TusUpload(file).apply {
                metadata = mapOf("filename" to fileName)
            }

            // The first package needs to have the length header
            client.headers = mapOf("Upload-Length" to upload.size.toString())
            val uploader = client.beginOrResumeUploadFromURL(upload, URL(url))
            var bytes: Int
            do {
                bytes = uploader.uploadChunk()
                if (bytes > 0) {
                    val frac = uploader.offset.toFloat() / upload.size.toFloat()
                    _progress.value = (slotIdx + frac) / SLOT_COUNT
                }

                // Remove the length header after the first package
                client.headers = emptyMap()
            } while (bytes > 0)
            uploader.finish()
        }

    fun uploadAll(onDone: (String) -> Unit, onError: (Throwable) -> Unit) =
        viewModelScope.launch {
            try {
                val createRes = RetrofitProvider.api.createTask()
                val urls = createRes.upload_urls
                println(urls)
                val order = listOf("pre", "post", "shp", "shx")

                order.forEachIndexed { idx, label ->
                    uploadOne(slots.value[idx].uri!!, idx, urls[label]!!)
                }
                _progress.value = null
//                RetrofitProvider.api.startTask(createRes.task_id)
                println(createRes.task_id)
                onDone(createRes.task_id)
            } catch (e: Exception) {
                _progress.value = null
                onError(e)
            }
        }

    companion object {
        fun provideFactory(appCtx: android.content.Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UploadViewModel(appCtx.applicationContext) as T
                }
            }
    }

}
