package com.github.saintleva.sourcechew.di

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.data.storage.KSafeKeyValueStorage
import com.github.saintleva.sourcechew.data.storage.MultiplatformSettingsKeyValueStorage
import eu.anifantakis.lib.ksafe.KSafe
import org.koin.dsl.module


actual val platformModule = module {

    includes(webCommonModule)

    single<KSafe> { KSafe() }

    single<SecureKeyValueStorage> { KSafeKeyValueStorage(ksafe = get()) }
}