package com.github.saintleva.sourcechew.di

import android.content.Context
import kotlin.reflect.KClass

actual val platformExtraTypes: List<KClass<*>> = listOf(Context::class)
