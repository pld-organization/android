package com.example.sahtek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sahtek.navigation.AppNavGraph
import com.example.sahtek.ui.theme.SahtekTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SahtekTheme {
                AppNavGraph()
            }
        }
    }
}
