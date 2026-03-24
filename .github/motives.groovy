/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints

/**
 * This script is run using gplus in the script motives.sh.
 * This script saves a file with the lint name and extension .md to the tmp folder.
 * The motive of this lint is preserved as content.
 * These files are saved for all active lints in the project.
 * @todo #65:15min after fixing this problem with creation of groovy folder
 * https://github.com/jcabi/jcabi-parent/issues/586,
 * move this script to the src/main/groovy folder in package org.eolang.lints
 * @todo #65:15min after moving motives.groovy into src/main/groovy we should add test,
 * that verifies that this script correctly saves a list of all lint motifs to the tmp folder
 */

static def saveLint(Lint<?> lint) {
    new File("tmp/${lint.name()}.md").text = lint.motive()
}

new PkMono().forEach {
    saveLint(it)
}

new PkWpa().forEach {
    saveLint(it)
}
