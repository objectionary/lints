/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
/**
 * @todo #15:45min Add eslint to repository https://github.com/eslint/eslint.
 * We should integrate eslint into repository to monitor the quality of code written in java script.
 */
import { lint as lintSync } from "markdownlint/sync";

globalThis.lint = function(text) {
  const options = {
    "strings": {
      "file": text
    }
  };

  const results = lintSync(options);
  return results["file"];
}
