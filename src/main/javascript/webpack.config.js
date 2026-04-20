/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
import path from 'path';
import { fileURLToPath } from 'url';

export default {
  entry: './markdownlint.js',
  output: {
    filename: '../classes/markdownlint.js',
    path: path.dirname(fileURLToPath(import.meta.url)),
  },
  mode: 'production',
  target: ['webworker'],
  resolve: {
    fallback: {
      fs: false,
      path: false,
      os: false,
      assert: false,
    },
  }
};
