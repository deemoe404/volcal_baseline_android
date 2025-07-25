package com.example.client_volcal_baseline.ui.csv

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import com.example.client_volcal_baseline.network.RetrofitProvider

import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider

@Composable
private fun FullScreenImage(
    url: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            ZoomableAsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CsvScreen(
    resultKey: String,
    onBack: () -> Unit = {}
) {
    var fullImageUrl by remember { mutableStateOf<String?>(null) }

    val ctx = LocalContext.current
    val vm: CsvViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(cls: Class<T>): T =
            CsvViewModel(resultKey) as T
    })

    val rows by vm.items.collectAsStateWithLifecycle()
    val checked by vm.checked.collectAsStateWithLifecycle()
    val total by vm.total.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("CSV 结果") }) },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { vm.selectAll() }) { Text("Select All") }
                Button(onClick = { vm.clearAll() })  { Text("Clear") }
                Text("Total Vol: %.3f m³".format(total))
            }
        }
    ) { pad ->
        LazyColumn(contentPadding = pad) {
            itemsIndexed(rows) { idx, item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { vm.toggle(idx) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checked.getOrNull(idx) ?: false,
                        onCheckedChange = { vm.toggle(idx) }
                    )
                    Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                        Text(item.id, style = MaterialTheme.typography.bodyLarge)
                        Text("area=${item.area}, cut=${item.cut}, fill=${item.fill}, net=${item.net}")
                    }
                    val imgUrl by produceState<String?>(null, item.imageKey) {
                        value = try {
                            RetrofitProvider.api.presign(item.imageKey).url
                        } catch (e: Exception) { null }
                    }
                    AsyncImage(
                        model = imgUrl,
                        contentDescription = item.id,
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.LightGray)
                            .clickable {
                                imgUrl?.let { fullImageUrl = it }
                            }
                    )
                }
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                fullImageUrl?.let { url ->
                    FullScreenImage(url = url) { fullImageUrl = null }
                }
            }
        }
    }
}
