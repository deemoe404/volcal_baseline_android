package com.example.client_volcal_baseline.ui.upload

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.DecimalFormat

@Composable
fun UploadScreen(
    vm: UploadViewModel,
    onNavigate: (String) -> Unit,
    onHistoryClick: (String) -> Unit = onNavigate
) {
    val slots by vm.slots.collectAsStateWithLifecycle()
    val progress by vm.progress.collectAsStateWithLifecycle()
    val ready by vm.ready.collectAsStateWithLifecycle()
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    val df = remember { DecimalFormat("#,##0.# KB") }

    val labels = listOf("Reference Point Cloud", "Changed Point Cloud", "Stable Area (.shp)", "Stable Area (.shx)")

    var currentIdx by remember { mutableStateOf(-1) }
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val size = ctx.contentResolver.openAssetFileDescriptor(it, "r")?.length ?: 0L
            vm.setUri(currentIdx, it, size)
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(Modifier.weight(1f)) {
                itemsIndexed(slots) { idx, slot ->
                    val btnText = labels.getOrElse(idx) { "Select File ${idx + 1}" }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { currentIdx = idx; picker.launch(arrayOf("*/*")) },
                            enabled = progress == null
                        ) { Text(btnText) }

                        Spacer(Modifier.width(12.dp))
                        Text(
                            slot.uri?.lastPathSegment?.let {
                                "$it (${df.format((slot.size ?: 0) / 1024.0)})"
                            } ?: "No file selected"
                        )
                    }
                    Divider()
                }
            }

            Button(
                onClick = {
                    vm.uploadAll(onNavigate) { e ->
                        Toast.makeText(ctx, "Upload Failed：${e.message}", Toast.LENGTH_LONG).show()
                    }
                },
                enabled = ready,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Next") }

            Spacer(Modifier.height(16.dp))
            Text("Previous Tasks", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
            ) {
                items(tasks) { id ->
                    Text(
                        text = id,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onHistoryClick(id) }
                            .padding(vertical = 8.dp)
                    )
                    Divider()
                }
            }
        }

        progress?.let {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(progress = it)
            }
        }
    }
}
