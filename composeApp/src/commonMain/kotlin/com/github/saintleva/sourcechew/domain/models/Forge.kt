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

package com.github.saintleva.sourcechew.domain.models


sealed class Forge(val name: String, val supportGroups: Boolean) {
    data object Github : Forge("GitHub", true)
    data object Gitlab : Forge("GitLab", true)
    data object Bitbucket : Forge("Bitbucket", false)
    data object Sourceforge : Forge("SourceForge", false)
    data object Gitea : Forge("Gitea", false)
    data object Sourcehut : Forge("SourceHut", false)
    data object Gogs : Forge("Gogs", false)
    data object Codeberg : Forge("Codeberg", false)

    companion object {
        val list = listOf(Github, Gitlab, Bitbucket, Sourceforge, Gitea, Sourcehut, Gogs, Codeberg)
    }
}