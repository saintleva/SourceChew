package com.github.saintleva.sourcechew.di

import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.Module


const val PREFS_DATA_STORE_FILE_NAME = "prefs.preferences_pb"

expect val platformModule: Module

expect val ioDispatcher: CoroutineDispatcher