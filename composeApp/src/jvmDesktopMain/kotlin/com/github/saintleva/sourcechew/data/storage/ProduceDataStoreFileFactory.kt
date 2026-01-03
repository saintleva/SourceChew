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

import org.kohsuke.github.GitHubBuilder


val a = GitHubBuilder()

//TODO: Use right filepath on desktop
private const val APP_PREFERENCE_DATASTORE = "asdfasdfasdfasdf"

//actual fun dataStorePreferences(
//    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>?,
//    coroutineScope: CoroutineScope,
//    migrations: List<DataMigration<Preferences>>,
//    context: Any?
//): DataStore<Preferences> = createDataStore(
//    corruptionHandler = corruptionHandler,
//    coroutineScope = coroutineScope,
//    migrations = migrations,
//    path = { _ ->
//        APP_PREFERENCE_DATASTORE
//    }
//)