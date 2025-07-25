package com.example.client_volcal_baseline.ui.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun StatusScreen(
    taskId: String,
    onDone: (String) -> Unit
) {
    val ctx           = LocalContext.current
    val vm            = remember { StatusViewModel(taskId) }
    val state by vm.state.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        vm.startPolling(lifecycleOwner.lifecycle)
    }

    LaunchedEffect(state.status) {
        if (state.status == "done") {
            onDone(taskId)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Task Status：${state.status}")
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { vm.manualRefresh() },
                enabled = !state.loading
            ) { Text("Reload") }
        }

        if (state.loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.4f)),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }
    }
}

