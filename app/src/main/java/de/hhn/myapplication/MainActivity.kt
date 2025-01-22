package de.hhn.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.hhn.myapplication.ui.screen.Dashboard

/**
 * MainActivity serves as the entry point for the application, setting up the user interface
 * using Jetpack Compose.
 * This activity is launched when the application starts and displays the Dashboard screen.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dashboard()
        }
    }
}