package com.github.saintleva.sourcechew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO: Remove this
//        val isDarkTheme: Boolean = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
//            Configuration.UI_MODE_NIGHT_YES -> true
//            Configuration.UI_MODE_NIGHT_NO,
//            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
//            else -> false
//        }

//        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()

        //TODO: Remove this
//        enableEdgeToEdge(
//            statusBarStyle = if (isDarkTheme) {
//                SystemBarStyle.dark(scrim = Color.Transparent.toArgb())
//            }
//            else {
//                SystemBarStyle.light(
//                    scrim = Color.Transparent.toArgb(),
//                    darkScrim = Color.Transparent.toArgb()
//                )
//            }
//        )

        setContent {
            App()
        }
    }
}