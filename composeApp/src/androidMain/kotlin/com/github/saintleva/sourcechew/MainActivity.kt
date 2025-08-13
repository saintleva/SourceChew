package com.github.saintleva.sourcechew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import io.github.aakira.napier.Napier


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Napier.d(tag = "MainActivity") { "OnCreate() started" }
        super.onCreate(savedInstanceState)
        viewModel //TODO: Implement good using of "viewModel"
        Napier.d(tag = "MainActivity") { "Before setContent()" }
        setContent {
            App()
        }
    }
}