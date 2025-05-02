/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints

import java.nio.file.Paths

new ReserveHome().exec(Paths.get("downloaded", "home").toString())
