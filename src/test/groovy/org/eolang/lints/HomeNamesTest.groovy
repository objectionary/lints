/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints

import com.yegor256.MayBeSlow
import java.nio.file.Path
import java.nio.file.Paths
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir

/**
 * Tests for {@link HomeNames}.
 * @since 0.0.49
 */
@Timeout(90L)
@Tag("deep")
@ExtendWith(MayBeSlow)
final class HomeNamesTest {

  @Test
  void placesHomeObjectsAsReserved(@TempDir final Path temp) {
    final String csv = temp.resolve("reserved.csv").toString()
    new HomeNames(csv, homeLocation()).placeCsv();
    MatcherAssert.assertThat(
      "Reserved objects are empty, but they should not",
      new ReservedNames(csv),
      Matchers.aMapWithSize(Matchers.greaterThan(0))
    )
  }

  @Test
  void placesHomeObjectsWithCorrectNames(@TempDir final Path temp) {
    final String csv = temp.resolve("reserved.csv").toString()
    new HomeNames(csv, homeLocation()).placeCsv();
    MatcherAssert.assertThat(
      "Home objects do not match with expected format",
      new ReservedNames(csv).values(),
      Matchers.everyItem(
        Matchers.hasToString(
          Matchers.matchesRegex(
            "org\\.eolang(?:\\.[a-zA-Z_][a-zA-Z0-9_-]*)+\\.eo"
          )
        )
      )
    )
  }

  /**
   * Home location.
   *
   * @return Home objects path as string
   */
  private static String homeLocation() {
    return Paths.get("downloaded", "home").toString()
  }
}
