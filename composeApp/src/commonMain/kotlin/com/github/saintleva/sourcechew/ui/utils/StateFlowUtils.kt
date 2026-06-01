package com.github.saintleva.sourcechew.ui.utils

import kotlinx.coroutines.flow.SharingStarted

/**
 * Common [SharingStarted] strategy for ViewModels.
 * 
 * When the UI stops observing, upstream flows stay active for 5 seconds to allow the system to
 * come back from a short-lived configuration change (such as rotations). If the UI stops
 * observing for longer, the cache is kept but the upstream flows are stopped.
 */
val WhileUiSubscribed: SharingStarted = SharingStarted.WhileSubscribed(5_000L)
