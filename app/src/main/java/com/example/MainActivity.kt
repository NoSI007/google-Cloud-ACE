package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.AceViewModel
import com.example.ui.navigation.AceMainAppScreen
import com.example.ui.theme.AceCloudGuideTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AceCloudGuideTheme {
                AceMainAppScreen(viewModel = viewModel)
            }
        }
    }
}
