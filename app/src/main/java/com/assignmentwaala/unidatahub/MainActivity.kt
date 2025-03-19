package com.assignmentwaala.unidatahub

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.assignmentwaala.unidatahub.presentation.App
import com.assignmentwaala.unidatahub.ui.theme.UniDataHubTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.TRANSPARENT
            ),
        )
        setContent {
            UniDataHubTheme {
               App()
            }
        }
    }
    companion object {
        val TAG = "UniDataHubDebug"
    }
}