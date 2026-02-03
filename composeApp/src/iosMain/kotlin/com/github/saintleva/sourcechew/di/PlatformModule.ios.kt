package com.github.saintleva.sourcechew.di

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module


//TODO: Implement real DI
actual val platformModule = module {}

actual val ioDispatcher = Dispatchers.Default