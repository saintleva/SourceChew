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

package com.github.saintleva.sourcechew.ui.search

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel


sealed class Hosting(val name: String) {
    object Github : Hosting("GitHub")
    object Gitlab : Hosting("GitLab")
    object Bitbucket : Hosting("Bitbucket")

    companion object {
        val list = listOf(Github, Gitlab, Bitbucket)
    }
}

typealias HostingOptions = Map<Hosting, Boolean>

//class HostingOptions(
//    val github: Boolean = true,
//    val gitlab: Boolean = true,
//    val bitbucket: Boolean = true
//)

//class TypeOptions(
//    val repository: Boolean = true,
//    val user: Boolean = false,
//    val groud: Boolean = false
//)

class SearchViewModel() : ViewModel() {

    val selectedHostings = mutableStateMapOf() HostingOptions()
//    val selectedTypes = TypeOptions()


}