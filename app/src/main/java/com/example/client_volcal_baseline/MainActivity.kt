package com.example.client_volcal_baseline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.client_volcal_baseline.ui.csv.CsvScreen
import com.example.client_volcal_baseline.ui.status.StatusScreen
import com.example.client_volcal_baseline.ui.upload.UploadScreen
import com.example.client_volcal_baseline.ui.upload.UploadViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AppNavRoot()
            }
        }
    }
}

@Composable
private fun AppNavRoot() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "upload") {

        /** 界面 1 —— 选择 & 上传 **/
        composable("upload") {
            // 直接把 Activity 作为 context 传给 ViewModel（简单起步）
            val vm: UploadViewModel = viewModel(factory = UploadViewModel.provideFactory(LocalContext.current))
            UploadScreen(
                vm,
                onNavigate = { taskId -> nav.navigate("status/$taskId") },
                onHistoryClick = { taskId -> nav.navigate("status/$taskId") }
            )
        }

        /** 界面 2 —— 轮询任务 **/
        composable("status/{taskId}") { backStack ->
            val taskId = backStack.arguments?.getString("taskId")!!
            StatusScreen(taskId) {
                nav.navigate("csv/$taskId") {
                    popUpTo("upload") { inclusive = false } // 保留回栈
                }
            }
        }

        /** 界面 3 —— CSV 展示（先占位） **/
        composable("csv/{taskId}") { back ->
            CsvScreen(back.arguments?.getString("taskId")!!)
        }
    }
}
