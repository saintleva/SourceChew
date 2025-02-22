/*
 * Copyright (C) Anton Liaukevich 2021-2022 <leva.dev@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.saintleva.sourcechew.data.storage

import okio.Path


internal const val dataStoreFileName = "preferences_pb"

//val dateStore: DataStore<Preferences> by preferencesDataStore()
//
//internal fun createDataStore(
//    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
//    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
//    migrations: List<DataMigration<Preferences>> = listOf(),
//    context: Any? = null,
//    path: (context: Any?) -> String,
//) = PreferenceDataStoreFactory.createWithPath(
//    corruptionHandler = corruptionHandler,
//    scope = coroutineScope,
//    migrations = migrations,
//    produceFile = {
//        path(context).toPath()
//    }
//)

expect class ProduceDataStoreFileFactory {
    operator fun invoke(): () -> Path
}

//fun createDataStore(
//    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
//    migrations: List<DataMigration<Preferences>> = listOf(),
//    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
//    produceFile: () -> String
//): DataStore<Preferences> =
//    PreferenceDataStoreFactory.createWithPath(
//        corruptionHandler = corruptionHandler,
//        migrations = listOf(),
//        scope = scope,
//        produceFile = { producePath().toPath() }
//    )
