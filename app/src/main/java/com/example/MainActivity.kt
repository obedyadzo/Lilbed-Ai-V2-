package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.LilbedDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LilbedViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Instantiating the unified viewmodel
    val viewModel = ViewModelProvider(this)[LilbedViewModel::class.java]

    setContent {
      MyApplicationTheme {
        LilbedDashboardScreen(viewModel = viewModel)
      }
    }
  }
}
