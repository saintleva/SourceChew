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

package com.github.saintleva.sourcechew.ui.style

import sourcechew.composeapp.generated.resources.Res
import sourcechew.composeapp.generated.resources.bitbucket_logo
import sourcechew.composeapp.generated.resources.github_logo_128px
import sourcechew.composeapp.generated.resources.gitlab_logo


//@Composable
//fun forgeIcons() = mapOf<Forge, Painter>(
//    Forge.Github to painterResource(Res.drawable.github_logo_512px),
//    Forge.Gitlab to painterResource(Res.drawable.github_logo_64px),
//    Forge.Bitbucket to painterResource(Res.drawable.bitbucket_logo)
//)

val forgeIconResources = mapOf(
    Forge.Github to Res.drawable.github_logo_128px,
    Forge.Gitlab to Res.drawable.gitlab_logo,
    Forge.Bitbucket to Res.drawable.bitbucket_logo
)
